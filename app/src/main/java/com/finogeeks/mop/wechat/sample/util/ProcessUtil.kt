package com.finogeeks.mop.wechat.sample.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process

/**
 * 获取当前进程名
 */
fun getCurrentProcessName(context: Context): String? {
    return getProcessName(context, Process.myPid())
}

/**
 * 获取指定进程id[processId]的进程名称
 */
fun getProcessName(context: Context, processId: Int): String? {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    return manager?.runningAppProcesses?.firstOrNull { runningAppProcessInfo -> runningAppProcessInfo.pid == processId }?.processName
}

/**
 * 判断指定进程id[processId]的进程是否存在
 */
fun isProcessExist(context: Context, processId: Int): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    return manager?.runningAppProcesses?.firstOrNull { runningAppProcessInfo -> runningAppProcessInfo.pid == processId } != null
}