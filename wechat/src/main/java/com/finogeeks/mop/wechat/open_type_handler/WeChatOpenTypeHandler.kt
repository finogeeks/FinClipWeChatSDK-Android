package com.finogeeks.mop.wechat.open_type_handler

import android.graphics.Bitmap
import android.os.Bundle
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import com.finogeeks.lib.applet.sdk.api.IAppletOpenTypeHandler
import com.finogeeks.mop.wechat.WeChatSDKManager
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
        val currentAppletInfo = FinAppClient.appletApiManager.getAppletInfo(currentAppletId)
        if (currentAppletInfo == null) {
            callback.onFailure()
            return
        }
        val wechatLoginInfo = currentAppletInfo.wechatLoginInfo
        if (wechatLoginInfo == null) {
            callback.onFailure()
            return
        }
        if (wechatLoginInfo.wechatOriginId.isEmpty() || wechatLoginInfo.phoneUrl.isEmpty()) {
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