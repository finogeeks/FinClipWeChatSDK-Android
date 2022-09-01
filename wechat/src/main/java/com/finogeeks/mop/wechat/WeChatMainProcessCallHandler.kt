package com.finogeeks.mop.wechat

import com.finogeeks.lib.applet.client.FinAppProcessClient
import com.finogeeks.lib.applet.interfaces.FinCallback
import com.finogeeks.lib.applet.sdk.api.IAppletProcessApiManager
import com.google.gson.Gson

internal class WeChatMainProcessCallHandler : IAppletProcessApiManager.MainProcessCallHandler {

    companion object {
        const val API_NAME_GET_FIN_APP_INFO = "getFinAppInfo"
    }

    override fun onMainProcessCall(name: String, params: String?, callback: FinCallback<String>?) {
        when (name) {
            API_NAME_GET_FIN_APP_INFO -> getFinAppInfo(callback)
        }
    }

    /**
     * 从小程序进程中获取 FinAppInfo
     */
    private fun getFinAppInfo(callback: FinCallback<String>?) {
        val finAppInfo = FinAppProcessClient.appletProcessApiManager.getAppletInfo()
        if (finAppInfo != null) {
            callback?.onSuccess(Gson().toJson(finAppInfo))
        } else {
            callback?.onError(-1, "")
        }
    }

}