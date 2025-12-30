package com.finogeeks.mop.wechat.open_type_handler

import android.graphics.Bitmap
import android.os.Bundle
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.modules.log.FLog
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
        val implementedByDelegate = (WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler != null
                && WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler!!.getPhoneNumber(callback))
        if (implementedByDelegate) {
            FLog.d(TAG, "[getPhoneNumber]宿主app实现了微信扩展SDK的getPhoneNumber代理方法")
            return
        }

        val currentAppletId = FinAppClient.appletApiManager.getCurrentAppletId()
        if (currentAppletId.isNullOrEmpty()) {
            FLog.i(TAG, "[getPhoneNumber]getCurrentAppletId()获取当前小程序id为null")
            callback.onFailure()
            return
        }
        val currentAppletInfo = FinAppClient.appletApiManager.getAppletInfoFromRunning(currentAppletId)
        if (currentAppletInfo == null) {
            FLog.i(TAG, "[getPhoneNumber]getAppletInfoFromRunning(currentAppletId)获取的小程序对象为null")
            callback.onFailure()
            return
        }
        val wechatLoginInfo = currentAppletInfo.wechatLoginInfo
        if (wechatLoginInfo == null) {
            FLog.i(TAG, "[getPhoneNumber]wechatLoginInfo为null")
            callback.onFailure()
            return
        }
        if (wechatLoginInfo.wechatOriginId.isEmpty() || wechatLoginInfo.phoneUrl.isEmpty()) {
            FLog.i(TAG, "[getPhoneNumber]wechatOriginId:" + wechatLoginInfo.wechatOriginId + ",phoneUrl:" + wechatLoginInfo.phoneUrl)
            callback.onFailure()
            return
        }
        if (!WeChatSDKManager.instance.isWXAppInstalled()) {
            FLog.i(TAG, "[getPhoneNumber] WeChat app is not installed")
            callback.onFailure()
            return
        }
        val appletType = when (currentAppletInfo.appType) {
            "trial" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
            "release" -> WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
            else -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
        }
        WeChatSDKManager.instance.getUserProfileCallback = null
        WeChatSDKManager.instance.getPhoneNumberCallback = callback
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


    companion object {

        private const val TAG = "WeChatOpenTypeHandler"

    }
}