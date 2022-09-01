package com.finogeeks.mop.wechat

import com.finogeeks.lib.applet.client.FinAppProcessClient
import com.finogeeks.lib.applet.interfaces.FinCallback
import com.finogeeks.lib.applet.sdk.api.IAppletProcessApiManager
import com.google.gson.Gson
import org.json.JSONObject

internal class WeChatMainProcessCallHandler : IAppletProcessApiManager.MainProcessCallHandler {

    companion object {
        const val CALL_NAME = "weChatSDK"
        const val API_NAME_GET_FIN_APP_INFO = "getFinAppInfo"
    }

    override fun onMainProcessCall(name: String, params: String?, callback: FinCallback<String>?) {
        if (CALL_NAME != name) {
            return
        }
        val apiName = try {
            JSONObject(params ?: "{}").optString("apiName")
        } catch (ignore: Exception) {
            ""
        }
        when (apiName) {
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