package com.finogeeks.mop.wechat.sample.util

import java.io.Closeable
import java.io.IOException

fun closeAll(vararg closeables: Closeable?) {
    closeables.forEach { closeable ->
        try {
            closeable?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}