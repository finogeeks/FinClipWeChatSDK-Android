package com.finogeeks.mop.wechat.apis

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.finogeeks.lib.applet.api.AppletApi
import com.finogeeks.lib.applet.api.apiFail
import com.finogeeks.lib.applet.interfaces.ICallback
import com.finogeeks.lib.applet.main.FinAppHomeActivity
import com.finogeeks.mop.wechat.BuildConfig
import com.finogeeks.mop.wechat.WeChatSDKManager
import com.finogeeks.mop.wechat.utils.WeChatAppletProcessUtils
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import org.json.JSONObject

/**
 * 微信相关API
 */
class WeChatPlugin(activity: Activity) : AppletApi(activity) {

    private val weChatSDKManager by lazy {
        WeChatSDKManager.instance.init(activity)
        WeChatSDKManager.instance
    }

    private var broadcastReceiver: BroadcastReceiver? = null

    /**
     * 记录当次正在进行的callback
     */
    private var currentCallback: ICallback? = null

    /**
     * 记录当次正在进行的event
     */
    private var currentEvent: String? = null

    override fun onDestroy() {
        broadcastReceiver?.let {
            (context as? Activity)?.unregisterReceiver(it)
        }
        super.onDestroy()
    }


    override fun apis(): Array<String> {
        return arrayOf(
            API_WECHAT_LOGIN,
            API_WECHAT_REQUEST_PAYMENT,
            API_WECHAT_MINI_PROGRAM
        )
    }

    override fun invoke(appId: String, event: String, param: JSONObject, callback: ICallback) {
        registerBroadcastReceiver()
        val activity = context as FinAppHomeActivity
        val finAppInfo = activity.mFinAppInfo
        val appletType = when (finAppInfo.appType) {
            "trial" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
            "release" -> WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
            else -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
        }
        val wechatLoginInfo = finAppInfo.wechatLoginInfo
        currentEvent = event
        currentCallback = callback
        weChatSDKManager.getPhoneNumberCallback = null
        weChatSDKManager.getUserProfileCallback = null
        when (event) {
            API_WECHAT_LOGIN -> {
                if (wechatLoginInfo == null) {
                    callback.onFail(apiFail(event))
                    return
                }
                if (wechatLoginInfo.wechatOriginId.isEmpty()) {
                    callback.onFail(apiFail(event, "originId not exist"))
                    return
                }
                if (wechatLoginInfo.profileUrl.isEmpty()) {
                    callback.onFail(apiFail(event, "path not exist"))
                    return
                }
                weChatSDKManager.launchGetProfileWxMiniProgram(
                    appletType,
                    activity.mFinAppInfo.wechatLoginInfo
                )
            }

            API_WECHAT_REQUEST_PAYMENT -> {
                if (wechatLoginInfo == null) {
                    callback.onFail(apiFail(event))
                    return
                }
                if (wechatLoginInfo.wechatOriginId.isEmpty()) {
                    callback.onFail(apiFail(event, "originId not exist"))
                    return
                }
                if (wechatLoginInfo.paymentUrl.isEmpty()) {
                    callback.onFail(apiFail(event, "path not exist"))
                    return
                }
                weChatSDKManager.launchRequestPaymentWxMiniProgram(
                    appletType,
                    activity.mFinAppInfo.wechatLoginInfo,
                    param
                )
            }

            API_WECHAT_MINI_PROGRAM -> {
                val wxMiniProgramOriginId = param.optString("originId")
                if (wxMiniProgramOriginId.isEmpty()) {
                    callback.onFail(apiFail(event, "originId not exist"))
                    return
                }
                val envVersion = when (param.optString("envVersion")) {
                    "develop" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
                    "trial" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
                    else -> WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
                }
                var path = param.optString("path")
                if (path == "null") {
                    path = ""
                }
                weChatSDKManager.launchWXMiniProgram(wxMiniProgramOriginId, path, envVersion)
            }
        }
    }

    /**
     * 在小程序进程中接收主进程的微信回调广播处理后续逻辑
     */
    private fun registerBroadcastReceiver() {
        if (broadcastReceiver != null) {
            return
        }
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(mContext: Context, intent: Intent) {
                currentCallback?.let { callback ->
                    currentEvent?.let { event ->
                        WeChatAppletProcessUtils.moveAppletProcessToFront(context)
                        handleIntentResult(event, callback, intent)
                        currentCallback = null
                        currentEvent = null
                    }
                }
            }
        }.also {
            (context as? Activity)?.registerReceiver(it, IntentFilter(WECHAT_BROADCAST_ACTION))
        }
    }

    /**
     * 处理由 FinWxEntryActivity 回传过来的数据
     */
    private fun handleIntentResult(event: String, callback: ICallback, intent: Intent) {
        val type = intent.getIntExtra(KEY_TYPE, -1)
        val errCode = intent.getIntExtra(KEY_ERROR_CODE, BaseResp.ErrCode.ERR_USER_CANCEL)
        if (errCode != BaseResp.ErrCode.ERR_OK) {
            callback.onFail(apiFail(event))
        } else {
            if (type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
                val extraData: String? = intent.getStringExtra(KEY_EXTRA_DATA)
                if (!extraData.isNullOrEmpty()) {
                    if (extraData.contains(":fail")) {
                        // 失败的extraData里已为完整的失败信息json，直接回调fail即可
                        callback.onFail(JSONObject(extraData))
                    } else {
                        callback.onSuccess(JSONObject(extraData))
                    }
                } else {
                    callback.onFail(apiFail(event))
                }
            }
        }
    }

    companion object {
        private const val API_WECHAT_LOGIN = "login"
        private const val API_WECHAT_REQUEST_PAYMENT = "requestPayment"
        private const val API_WECHAT_MINI_PROGRAM = "navigateToWechatMiniProgram"

        const val WECHAT_BROADCAST_ACTION = BuildConfig.LIBRARY_PACKAGE_NAME
        const val KEY_TYPE = "type"
        const val KEY_ERROR_CODE = "errCode"
        const val KEY_EXTRA_DATA = "extraData"
    }
}