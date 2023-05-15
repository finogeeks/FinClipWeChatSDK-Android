package com.finogeeks.mop.wechat

import android.content.Context
import android.content.Intent

object FinWeChatWXEntry {
    fun handleWeChatIntent(context: Context, intent: Intent?) {
        if (intent != null) {
            WeChatSDKManager.instance.init(context)
            WeChatSDKManager.instance.handleIntent(intent)
        }
    }
}