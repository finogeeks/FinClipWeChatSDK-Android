package com.finogeeks.mop.wechat.userprofile

import android.content.Context
import com.finogeeks.lib.applet.client.FinAppInfo
import com.finogeeks.lib.applet.modules.userprofile.IUserProfileHandler
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import com.finogeeks.mop.wechat.WeChatSDKManager
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import org.json.JSONObject

class WeChatUserProfileHandler : IUserProfileHandler {

    override fun getUserProfileWithAppletInfo(
        context: Context,
        finAppInfo: FinAppInfo,
        callback: IUserProfileHandler.UserProfileCallback
    ) {
        WeChatSDKManager.instance.init(context)
        WeChatSDKManager.instance.getPhoneNumberCallback = null
        WeChatSDKManager.instance.getUserProfileCallback = callback
        val appletType = when (finAppInfo.appType) {
            "trial" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
            "release" -> WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
            else -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
        }
        val wechatLoginInfo = finAppInfo.wechatLoginInfo
        if (wechatLoginInfo == null) {
            callback.onError(null)
            return
        }
        if (wechatLoginInfo.wechatOriginId.isEmpty()) {
            callback.onError("originId not exist")
            return
        }
        if (wechatLoginInfo.profileUrl.isEmpty()) {
            callback.onError("path not exist")
            return
        }
        WeChatSDKManager.instance.launchGetProfileWxMiniProgram(
            appletType,
            wechatLoginInfo
        )
    }
}