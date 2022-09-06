package com.finogeeks.mop.wechat.sample

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
import android.widget.Toast
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.lib.applet.client.FinAppConfig
import com.finogeeks.lib.applet.client.FinAppConfig.UIConfig
import com.finogeeks.lib.applet.client.FinStoreConfig
import com.finogeeks.lib.applet.interfaces.FinCallback
import com.finogeeks.lib.applet.sdk.api.IAppletApiManager
import com.finogeeks.lib.applet.sdk.api.IAppletLifecycleCallback
import com.finogeeks.mop.wechat.sample.api.AppletApi
import com.finogeeks.mop.wechat.sample.util.longToastOnUiThread
import com.finogeeks.mop.wechat.sample.util.toastOnUiThread
import org.json.JSONObject

/**
 * 小程序SDK初始化类
 */
class FinAppletInitializer(private val application: Application) {

    /**
     * 标识SDK是否已初始化成功
     */
    var isInitSuccess = false

    /**
     * 初始化回调监听器集合
     */
    private val initCallbacks: MutableList<FinCallback<Any?>> by lazy {
        mutableListOf<FinCallback<Any?>>()
    }

    val APP_SDK_KEY = "4dKf86p1wMLVmKy3s/JoTAA="
    val APP_SDK_SECRET = "07b311bf9cb0cf4d"

    private val finStoreConfigs: List<FinStoreConfig> by lazy {
        listOf(
            FinStoreConfig(
                BuildConfig.SDK_KEY,
                BuildConfig.SDK_SECRET,
                BuildConfig.API_URL,
                BuildConfig.APM_URL,
                "",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://finchat-mop.finogeeks.club",
                "",
                "/api/v1/mop/",
                "40F807D4E7A3D6C3407FE7C9AF59EC1E556249BE45FDA23EF0EA4A8EEC523754",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://finchat-mop-saas.finogeeks.club",
                "",
                "/api/v1/mop/",
                "40F807D4E7A3D6C3407FE7C9AF59EC1E556249BE45FDA23EF0EA4A8EEC523754",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://finchat-mop-b.finogeeks.club",
                "",
                "/api/v1/mop/",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://finchat-mop-b-private.finogeeks.club",
                "",
                "/api/v1/mop/",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://mop-pufa-gateway.finogeeks.club",
                "",
                "/api/v1/mop/",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://finclip.com",
                "",
                "/api/v1/mop/",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://api.finclip.com",
                "",
                "/api/v1/mop/",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            ),
            FinStoreConfig(
                "22LyZEib0gLTQdU3MUauAfJ/xujwNfM6OvvEqQyH4igA",
                "703b9026be3d6bc5",
                "https://finclip-testing.finogeeks.club",
                "",
                "/api/v1/mop/",
                "",
                FinAppConfig.ENCRYPTION_TYPE_SM
            )
        )
    }

    fun init() {
        if (FinAppClient.isFinAppProcess(application)) {
            return
        }
        val uiConfig = UIConfig()
        uiConfig.isHideBackHome = true
        uiConfig.isHideForwardMenu = false
        uiConfig.isHideFeedbackAndComplaints = false
        uiConfig.isHideSettingMenu = false
        uiConfig.navigationBarTitleTextAppearance

        uiConfig.moreMenuStyle = UIConfig.MORE_MENU_DEFAULT

        val apmExtendInfo = mapOf(
            "key1" to "value1",
            "key2" to JSONObject().put("timestamp", System.currentTimeMillis()).put("name", "name"),
            "key3" to listOf(mapOf("mapKey1" to null, "mapKey2" to "mapValue2"), Integer.MAX_VALUE),
            "key4" to null,
            "key5" to Integer.MIN_VALUE,
            "key6" to mapOf("mapKey1" to Long.MAX_VALUE)
        )

        val config = FinAppConfig.Builder()
//                .setSdkKey(BuildConfig.SDK_KEY)
//                .setSdkSecret(BuildConfig.SDK_SECRET)
//                .setSdkKey(APP_SDK_KEY)
//                .setSdkSecret(APP_SDK_SECRET)
//                .setApiUrl(BuildConfig.API_URL)
//                .setApiPrefix(BuildConfig.API_PREFIX)
            .setDebugMode(/*BuildConfig.DEBUG*/true)
            .setUiConfig(uiConfig)
            .setApmExtendInfo(apmExtendInfo)
//                .setEncryptionType(ENCRYPTION_TYPE_SM)
            .setDisableRequestPermissions(false)
            .setNeedToRemoveCookiesDomains(listOf(""))
            .setDisableTbs(false)
            .setAppletIntervalUpdateLimit(3)
            .setMaxRunningApplet(1)
            .setPageCountLimit(5)
            .setBindAppletWithMainProcess(true)
            .setForegroundServiceConfig(
                FinAppConfig.ForegroundServiceConfig(
                    true,
                    R.drawable.ic_launcher_background,
                    "123",
                    "abc"
                )
            )
            .setWebViewMixedContentMode(MIXED_CONTENT_NEVER_ALLOW)
            .setFinStoreConfigs(finStoreConfigs)
            .setAppletText("快应用")
            .setEnableApmDataCompression(true)
            .setDisableGetSuperviseInfo(true)
            .setCustomWebViewUserAgent("android 7.1.1")
            .setUserId("13286836062")
            .setMinAndroidSdkVersion(Build.VERSION_CODES.LOLLIPOP)
            .build()

        FinAppClient.init(application, config, object : FinCallback<Any?> {
            override fun onSuccess(result: Any?) {
                Log.d(TAG, "init result : $result")
                FinAppClient.extensionApiManager.registerApi(AppletApi(application))
                FinAppClient.appletApiManager.setAppletLifecycleCallback(object :
                    IAppletLifecycleCallback {

                    override fun onCreate(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onCreate : $appId")

                    }

                    override fun onInitComplete(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onInitComplete : $appId")  }

                    override fun onStart(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onStart : $appId")
                    }

                    override fun onResume(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onResume : $appId")
                    }

                    override fun onPause(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onPause : $appId")
                    }

                    override fun onStop(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onStop : $appId")
                    }

                    override fun onDestroy(appId: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onDestroy : $appId")
                    }

                    override fun onFailure(appId: String, errMsg: String) {
                        Log.d(TAG, "IAppletLifecycleCallback onFailure : $appId ,$errMsg")
                    }
                })

                isInitSuccess = true
                initCallbacks.onEach {
                    onSuccess(result)
                }
            }

            override fun onError(code: Int, error: String) {
                application.longToastOnUiThread("SDK初始化失败${if (error.isBlank()) "" else ", $error"}")
                initCallbacks.onEach {
                    onError(code, error)
                }
            }

            override fun onProgress(status: Int, error: String) {
                initCallbacks.onEach {
                    onProgress(status, error)
                }
            }
        })

        FinAppClient.appletApiManager.setAppletProcessCallHandler(object :
            IAppletApiManager.AppletProcessCallHandler {
            override fun onAppletProcessCall(
                name: String,
                params: String?,
                callback: FinCallback<String>?
            ) {
                Toast.makeText(
                    application,
                    "小程序进程调用主进程:name:$name,params:$params",
                    Toast.LENGTH_SHORT
                ).show()

                callback?.onSuccess("返回结果给小程序进程")
            }
        })

        FinAppClient.appletApiManager.setAppletSessionCallback(object :
            IAppletApiManager.AppletSessionCallback {
            override fun onAppletSessionInvalid(appId: String) {
                Log.d(TAG, "onAppletSessionInvalid:$appId")
                application.toastOnUiThread("onAppletSessionInvalid:$appId")
            }
        })
    }

    fun addInitCallback(callback: FinCallback<Any?>) {
        initCallbacks.add(callback)
    }

    fun removeInitCallback(callback: FinCallback<Any?>) {
        initCallbacks.remove(callback)
    }

    companion object {

        private const val TAG = "FinAppletInitializer"
    }
}