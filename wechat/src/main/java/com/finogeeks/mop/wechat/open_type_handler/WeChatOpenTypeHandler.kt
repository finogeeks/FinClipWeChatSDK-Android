package com.finogeeks.mop.wechat.open_type_handler

import android.graphics.Bitmap
import android.os.Bundle
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppInfo
import com.finogeeks.lib.applet.interfaces.FinCallback
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import com.finogeeks.lib.applet.sdk.api.IAppletOpenTypeHandler
import com.finogeeks.mop.wechat.WeChatMainProcessCallHandler
import com.finogeeks.mop.wechat.WeChatSDKManager
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import org.json.JSONObject

internal class WeChatOpenTypeHandler : IAppletOpenTypeHandler {

    override fun chooseAvatar(callback: IAppletHandler.IAppletCallback) {
        WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler?.chooseAvatar(callback)
    }

    override fun contact(json: JSONObject): Boolean {
        return WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler?.contact(json) ?: false
    }

    override fun feedback(bundle: Bundle): Boolean {
        return WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler?.feedback(bundle) ?: false
    }

    override fun getPhoneNumber(callback: IAppletHandler.IAppletCallback) {
        val currentAppletId = FinAppClient.appletApiManager.getCurrentAppletId()
        if (currentAppletId == null || currentAppletId.isEmpty()) {
            callback.onFailure()
            return
        }
        FinAppClient.appletApiManager.callInAppletProcess(
            currentAppletId,
            WeChatMainProcessCallHandler.API_NAME_GET_FIN_APP_INFO,
            "",
            object : FinCallback<String> {
                override fun onSuccess(p0: String?) {
                    if (p0.isNullOrEmpty()) {
                        callback.onFailure()
                        return
                    }
                    val currentAppletInfo = try {
                        Gson().fromJson(p0, FinAppInfo::class.java)
                    } catch (ignore: Exception) {
                        callback.onFailure()
                        return
                    }
                    val wechatLoginInfo = currentAppletInfo.wechatLoginInfo
                    if (wechatLoginInfo == null) {
                        callback.onFailure()
                        return
                    }
                    if (wechatLoginInfo.wechatOriginId.isNullOrEmpty() ||
                        wechatLoginInfo.phoneUrl.isNullOrEmpty()
                    ) {
                        callback.onFailure()
                        return
                    }
                    val appletType = when (currentAppletInfo.appType) {
                        "trial" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
                        "release" -> WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
                        else -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
                    }
                    WeChatSDKManager.instance.appletHandlerCallback = callback
                    WeChatSDKManager.instance.launchGetPhoneNumberWxMiniProgram(
                        appletType,
                        wechatLoginInfo
                    )
                }

                override fun onError(p0: Int, p1: String?) {
                    callback.onFailure()
                    return
                }

                override fun onProgress(p0: Int, p1: String?) {

                }
            })
    }

    override fun launchApp(appParameter: String?): Boolean {
        return WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler?.launchApp(appParameter)
            ?: false
    }

    override fun shareAppMessage(
        appInfo: String,
        bitmap: Bitmap?,
        callback: IAppletHandler.IAppletCallback
    ) {
        WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler?.shareAppMessage(
            appInfo,
            bitmap,
            callback
        )
    }
}