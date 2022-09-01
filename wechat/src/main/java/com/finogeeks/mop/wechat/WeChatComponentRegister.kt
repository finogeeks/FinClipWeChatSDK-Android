package com.finogeeks.mop.wechat

import android.content.Context
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.sdk.api.AbsComponentRegister
import com.finogeeks.mop.wechat.open_type_handler.WeChatOpenTypeHandler

internal class WeChatComponentRegister : AbsComponentRegister() {

    override fun onComponentRegister(context: Context) {
        WeChatSDKManager.instance.init(context)
        FinAppClient.appletApiManager.setAppletOpenTypeHandler(WeChatOpenTypeHandler())
    }
}