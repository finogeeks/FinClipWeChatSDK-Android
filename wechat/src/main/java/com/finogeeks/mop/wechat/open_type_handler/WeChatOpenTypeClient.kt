package com.finogeeks.mop.wechat.open_type_handler

class WeChatOpenTypeClient private constructor() {

    companion object {
        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WeChatOpenTypeClient()
        }
    }

    var iWeChatOpenTypeHandler: IWeChatOpenTypeHandler? = null
}