package com.finogeeks.mop.wechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class FinWXEntryActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleWeChatIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWeChatIntent(intent)
    }

    private fun handleWeChatIntent(intent: Intent?) {
        intent?.let {
            WeChatSDKManager.instance.init(this)
            WeChatSDKManager.instance.handleIntent(it)
            finish()
        }

    }
}