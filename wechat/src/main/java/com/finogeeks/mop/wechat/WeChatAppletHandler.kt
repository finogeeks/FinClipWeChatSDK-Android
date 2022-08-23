package com.finogeeks.mop.wechat

import android.graphics.Bitmap
import android.os.Bundle
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppInfo
import com.finogeeks.lib.applet.interfaces.FinCallback
import com.finogeeks.lib.applet.page.view.moremenu.MoreMenuItem
import com.finogeeks.lib.applet.rest.model.GrayAppletVersionConfig
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import org.json.JSONObject

/**
 * 由于 getPhoneNumber 是在主进程的 IAppletHandler 中被调用的，
 * 因此本SDK需要覆盖宿主APP的 IAppletHandler 实现
 */
class WeChatAppletHandler : IAppletHandler {

    override fun chooseAvatar(callback: IAppletHandler.IAppletCallback) {

    }

    override fun contact(json: JSONObject): Boolean {
        return false
    }

    override fun feedback(bundle: Bundle): Boolean {
        return false
    }

    override fun getGrayAppletVersionConfigs(appId: String): List<GrayAppletVersionConfig>? {
        return null
    }

    override fun getJSSDKConfig(json: JSONObject, callback: IAppletHandler.IAppletCallback) {

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

    override fun getRegisteredMoreMenuItems(appId: String): List<MoreMenuItem>? {
        return null
    }

    override fun getUserInfo(): Map<String, String>? {
        return null
    }

    override fun getWebViewCookie(appId: String): Map<String, String>? {
        return null
    }

    override fun launchApp(appParameter: String?): Boolean {
        return false
    }

    override fun onNavigationBarCloseButtonClicked(appId: String) {

    }

    override fun onRegisteredMoreMenuItemClicked(
        appId: String,
        path: String,
        menuItemId: String,
        appInfo: String?,
        bitmap: Bitmap?,
        callback: IAppletHandler.IAppletCallback
    ) {

    }

    override fun shareAppMessage(
        appInfo: String,
        bitmap: Bitmap?,
        callback: IAppletHandler.IAppletCallback
    ) {

    }
}