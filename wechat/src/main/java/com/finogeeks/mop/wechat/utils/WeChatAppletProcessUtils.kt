package com.finogeeks.mop.wechat.utils

import android.content.Context
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppProcessClient
import com.finogeeks.lib.applet.main.FinAppHomeActivity

object WeChatAppletProcessUtils {
    fun moveAppletProcessToFront(context: Context) {
        if (FinAppClient.isFinAppProcess(context.applicationContext)) {
            val activity = FinAppProcessClient.appletProcessActivity as? FinAppHomeActivity
                ?: return
            activity.moveTaskToFront()
        } else {
            val currentAppletId = FinAppClient.appletApiManager.getCurrentAppletId() ?: return
            FinAppClient.appletApiManager.moveTaskToFront(currentAppletId)
        }
    }
}