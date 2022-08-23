package com.finogeeks.mop.wechat

import android.content.Context
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppProcessClient
import com.finogeeks.lib.applet.sdk.api.AbsComponentRegister

class WeChatComponentRegister : AbsComponentRegister() {

    override fun onComponentRegister(context: Context) {
        WeChatSDKManager.instance.init(context)
        FinAppClient.appletApiManager.setAppletHandler(WeChatAppletHandler())
    }
}