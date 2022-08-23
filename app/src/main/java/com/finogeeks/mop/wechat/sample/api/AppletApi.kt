package com.finogeeks.mop.wechat.sample.api

import android.content.Context
import com.finogeeks.lib.applet.api.AbsApi
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppInfo
import com.finogeeks.lib.applet.interfaces.ICallback
import org.json.JSONObject

class AppletApi(private val context: Context) : AbsApi() {

    override fun apis(): Array<String> {
        return arrayOf("navigateToFinClipMiniProgram") // 管理小程序用到的
    }

    override fun invoke(event: String, param: JSONObject, callback: ICallback) {
        @Suppress("UNCHECKED_CAST")
        if (event == "navigateToFinClipMiniProgram") {
            val appId = param.optString("appId")
            val sequence = param.optInt("sequence", -1)
            val params = param.opt("params") as Map<String, String>?
            val apiServer = param.opt("apiServer") as String?
            val startParams = params?.let {
                FinAppInfo.StartParams(it["path"], it["query"], it["scene"])
            }
            if (apiServer != null) {
                FinAppClient.appletApiManager.startApplet(
                    context, apiServer, appId, sequence, startParams)
            } else {
                FinAppClient.appletApiManager.startApplet(context, appId, sequence, startParams)
            }
        }
    }
}