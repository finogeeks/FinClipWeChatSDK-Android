package com.finogeeks.mop.wechat

import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.sdk.api.AbsComponentRegister
import com.finogeeks.mop.wechat.open_type_handler.WeChatOpenTypeHandler
import com.finogeeks.mop.wechat.userprofile.WeChatUserProfileHandler

internal class WeChatComponentRegister : AbsComponentRegister() {

    override fun onComponentRegister(param: ComponentRegisterParam) {
        WeChatSDKManager.instance.init(param.context)
        FinAppClient.appletApiManager.setAppletOpenTypeHandler(WeChatOpenTypeHandler())
        if (param.finAppConfig.getUserProfileHandlerClass.isNullOrEmpty()) {
            param.finAppConfig.setGetUserProfileHandlerClass(WeChatUserProfileHandler::class.java)
        }
    }
}