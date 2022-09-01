package com.finogeeks.mop.wechat

import android.content.Context
import android.content.Intent
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.rest.model.WechatLoginInfo
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import com.finogeeks.mop.wechat.apis.WeChatPlugin
import com.finogeeks.mop.wechat.utils.WeChatAppletProcessUtils
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONObject
import java.lang.ref.WeakReference

internal class WeChatSDKManager private constructor() : IWXAPIEventHandler {

    companion object {
        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WeChatSDKManager()
        }
    }

    private var isInit = false
    private lateinit var iwxapi: IWXAPI
    private lateinit var contextRef: WeakReference<Context>

    /**
     * 用于处理getPhoneNumber，因getPhoneNumber是在主进程的AppletHandler种调用，
     * 因此也要原路回调回去
     */
    var appletHandlerCallback: IAppletHandler.IAppletCallback? = null

    fun init(context: Context) {
        if (isInit) {
            return
        }
        contextRef = WeakReference(context)
        iwxapi = WXAPIFactory.createWXAPI(
            context,
            context.resources.getString(R.string.wechat_sdk_app_id),
            true
        )
        iwxapi.registerApp(context.resources.getString(R.string.wechat_sdk_app_id))
        isInit = true
    }

    fun launchGetProfileWxMiniProgram(
        appletType: Int,
        wechatLoginInfo: WechatLoginInfo
    ) {
        val req = WXLaunchMiniProgram.Req()
        req.userName = wechatLoginInfo.wechatOriginId
        req.path = wechatLoginInfo.profileUrl
        req.miniprogramType = appletType
        req.extData
        iwxapi.sendReq(req)
    }

    fun launchGetPhoneNumberWxMiniProgram(
        appletType: Int,
        wechatLoginInfo: WechatLoginInfo
    ) {
        val req = WXLaunchMiniProgram.Req()
        req.userName = wechatLoginInfo.wechatOriginId
        req.path = wechatLoginInfo.phoneUrl
        req.miniprogramType = appletType
        req.extData
        iwxapi.sendReq(req)
    }

    fun launchRequestPaymentWxMiniProgram(
        appletType: Int,
        wechatLoginInfo: WechatLoginInfo,
        params: JSONObject
    ) {
        val req = WXLaunchMiniProgram.Req()
        req.userName = wechatLoginInfo.wechatOriginId
        val profileUrlSB = StringBuilder(wechatLoginInfo.paymentUrl)
        profileUrlSB.append("?type=${params.optString("type")}")
        profileUrlSB.append("&appId=${params.optString("appId")}")
        profileUrlSB.append("&nonceStr=${params.optString("nonceStr")}")
        profileUrlSB.append("&package=${params.optString("package")}")
        profileUrlSB.append("&paySign=${params.optString("paySign")}")
        profileUrlSB.append("&signType=${params.optString("signType")}")
        profileUrlSB.append("&timeStamp=${params.optString("timeStamp")}")
        req.path = profileUrlSB.toString()
        req.miniprogramType = appletType
        req.extData
        iwxapi.sendReq(req)
    }

    /**
     * 转交给[onResp]处理回调，注意，该方法将在主进程中执行
     */
    fun handleIntent(intent: Intent) {
        iwxapi.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {

    }

    /**
     * 将会在主进程中收到该回调
     */
    override fun onResp(resp: BaseResp) {
        // 该SDK目前仅处理启动微信小程序后的回调
        if (resp.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            val launchMiniProResp = resp as WXLaunchMiniProgram.Resp
            val extraData = launchMiniProResp.extMsg
            if (appletHandlerCallback != null) {
                // 直接在主进程使用 appletHandlerCallback 进行跨进程回调
                val currentAppletId = FinAppClient.appletApiManager.getCurrentAppletId()
                if (currentAppletId == null || currentAppletId.isEmpty()) {
                    appletHandlerCallback?.onFailure()
                } else {
                    // 将小程序移至前台
                    val activityName = FinAppClient.appletApiManager
                        .getAppletActivityName(currentAppletId)
                        ?.substringBefore("@") ?: ""
                    val context = contextRef.get()
                    if (activityName.isNotEmpty() && context != null) {
                        WeChatAppletProcessUtils.moveAppletProcessToFront(context, activityName)
                        if (extraData.contains(":fail")) {
                            appletHandlerCallback?.onFailure()
                        } else {
                            appletHandlerCallback?.onSuccess(JSONObject(extraData))
                        }
                    } else {
                        appletHandlerCallback?.onFailure()
                    }
                }
                appletHandlerCallback = null
            } else {
                // 使用广播发送给小程序进程
                contextRef.get()?.let {
                    val intent = Intent(WeChatPlugin.WECHAT_BROADCAST_ACTION)
                    intent.putExtra(WeChatPlugin.KEY_TYPE, resp.type)
                    intent.putExtra(WeChatPlugin.KEY_ERROR_CODE, resp.errCode)
                    if (extraData.isNotEmpty()) {
                        intent.putExtra(WeChatPlugin.KEY_EXTRA_DATA, extraData)
                    }
                    it.sendBroadcast(intent)
                }
            }

        }
    }

}