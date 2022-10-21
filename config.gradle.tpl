ext {
    android = [
            compileSdkVersion: 29,
            targetSdkVersion : 28,
            buildToolsVersion: "29.0.2",
            minSdkVersion    : 19,
    ]

    nexus_applet = [
            url     : "https://gradle.finogeeks.club/repository/applet/",
            username: "finclip",
            password: "Abcd@@1234",
    ]

    nexus_finogeeks = [
            url     : "https://gradle.finogeeks.club/repository/finogeeks/",
            username: "finclip",
            password: "Abcd@@1234",
    ]

    versions = [
            // 3.5.4是3.5.3的补丁版本，解决"Android 11 中软件包可见性"（Manifest <queries>标签）编译报错
            // https://developer.android.google.cn/studio/releases/gradle-plugin#4-0-0
            gradle          : '3.5.4',

            kotlin          : '1.3.61',
            kotlinPlugin    : '1.3.61',

            appcompat       : "23.0.0",
            recyclerview    : "23.2.0",
            constraintlayout: "1.1.3",

            finapplet       : "__finapplet_version__",

            leak_canary     : "2.7",

            bga_qrcode_zbar : "1.3.7",

            utilcode        : "1.23.4",

            wechat          : "6.8.0",
    ]

    dependencies = [
            kotlin          : "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}",

            recyclerview    : "com.android.support:recyclerview-v7:${versions.recyclerview}",
            appcompat       : "com.android.support:appcompat-v7:${versions.appcompat}",
            constraintlayout: "com.android.support.constraint:constraint-layout:${versions.constraintlayout}",

            finapplet       : "com.finogeeks.lib:finapplet:${versions.finapplet}",

            leak_canary     : "com.squareup.leakcanary:leakcanary-android:${versions.leak_canary}",

            bga_qrcode_zbar : "cn.bingoogolapple:bga-qrcode-zbar:${versions.bga_qrcode_zbar}",

            utilcode        : "com.blankj:utilcode:${versions.utilcode}",

            wechat          : "com.tencent.mm.opensdk:wechat-sdk-android:${versions.wechat}",
    ]
}