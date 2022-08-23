package com.finogeeks.mop.wechat.sample

import android.support.multidex.MultiDexApplication
import com.finogeeks.lib.applet.client.FinAppClient

class SampleApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (FinAppClient.isFinAppProcess(this)) {
            return
        }
        FinAppletInitializer(this).init()
    }
}