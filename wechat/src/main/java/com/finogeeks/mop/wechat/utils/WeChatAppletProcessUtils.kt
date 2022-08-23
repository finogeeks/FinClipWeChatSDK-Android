package com.finogeeks.mop.wechat.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object WeChatAppletProcessUtils {
    fun moveAppletProcessToFront(context: Context, activityName: String) {
        val moveToTopIntent = Intent(context, Class.forName(activityName))
        moveToTopIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            moveToTopIntent,
            0
        )
        pendingIntent.send()
    }
}