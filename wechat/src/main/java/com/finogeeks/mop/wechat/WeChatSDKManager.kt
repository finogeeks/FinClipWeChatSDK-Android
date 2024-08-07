package com.finogeeks.mop.wechat

import android.content.Context
import android.content.Intent
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.modules.common.broadcastPermission
import com.finogeeks.lib.applet.modules.userprofile.IUserProfileHandler
import com.finogeeks.lib.applet.rest.model.WechatLoginInfo
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import com.finogeeks.mop.wechat.apis.WeChatPlugin
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
     * 用于处理 getPhoneNumber 的结果回调
     */
    var getPhoneNumberCallback: IAppletHandler.IAppletCallback? = null

    /**
     * 用于处理 getUserProfile 的结果回调
     */
    var getUserProfileCallback: IUserProfileHandler.UserProfileCallback? = null

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
        iwxapi.sendReq(req)
    }

    fun launchWXMiniProgram(
        wxMiniProgramOriginId: String,
        path: String,
        envVersion: Int
    ) {
        val req = WXLaunchMiniProgram.Req()
        req.userName = wxMiniProgramOriginId
        if (path.isNotEmpty()) {
            req.path = path
        }
        req.miniprogramType = envVersion
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
     * 将小程序移至前台并回调结果
     */
    private fun callbackOnMainProcess(
        extraData: String,
        onSuccess: (result: JSONObject) -> Unit,
        onFail: () -> Unit
    ) {
        val currentAppletId = FinAppClient.appletApiManager.getCurrentAppletId()
        if (currentAppletId.isNullOrEmpty()) {
            onFail()
            return
        }
        val context = contextRef.get()
        if (context != null) {
            FinAppClient.appletApiManager.moveTaskToFront(currentAppletId)
            if (extraData.contains(":fail")) {
                onFail()
            } else {
                onSuccess(JSONObject(extraData))
            }
        } else {
            onFail()
        }
    }

    /**
     * 将会在主进程中收到该回调
     */
    override fun onResp(resp: BaseResp) {
        // 该SDK目前仅处理启动微信小程序后的回调
        if (resp.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            val launchMiniProResp = resp as WXLaunchMiniProgram.Resp
            val extraData = launchMiniProResp.extMsg
            if (getPhoneNumberCallback != null) {
                // 当 getPhoneNumberCallback 不为 null 时，
                // 说明此时调用的是 getPhoneNumber
                // 此时调用进程和回调进程均为主进程，直接使用 getPhoneNumberCallback 进行回调
                callbackOnMainProcess(
                    extraData = extraData,
                    onSuccess = { result ->
                        getPhoneNumberCallback?.onSuccess(result)
                    },
                    onFail = {
                        getPhoneNumberCallback?.onFailure()
                    }
                )
                getPhoneNumberCallback = null
            } else if (getUserProfileCallback != null) {
                // 当 getUserProfileCallback 不为 null 时，
                // 说明此时调用的是 getUserProfile，
                // 此时调用进程和回调进程均为主进程，直接使用 getUserProfileCallback 进行回调
                callbackOnMainProcess(
                    extraData = extraData,
                    onSuccess = { result ->
                        getUserProfileCallback?.onSuccess(result)
                    },
                    onFail = {
                        getUserProfileCallback?.onError(null)
                    }
                )
                getUserProfileCallback = null
            } else {
                // 以上条件未满足时，
                // 说明调用的是 WeChatPlugin 内的 api
                // 此时由于微信拉起 WxEntryActivity 的进程为主进程，而 callback 对象实例都在小程序进程内，
                // 为了统一处理，使用广播发送进行后续回调逻辑
                contextRef.get()?.let {
                    val intent = Intent(WeChatPlugin.WECHAT_BROADCAST_ACTION)
                    intent.putExtra(WeChatPlugin.KEY_TYPE, resp.type)
                    intent.putExtra(WeChatPlugin.KEY_ERROR_CODE, resp.errCode)
                    if (extraData.isNotEmpty()) {
                        intent.putExtra(WeChatPlugin.KEY_EXTRA_DATA, extraData)
                    }
                    it.sendBroadcast(intent, it.broadcastPermission())
                }
            }

        }
    }

}