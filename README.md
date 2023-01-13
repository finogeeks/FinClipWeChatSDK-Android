<p align="center">
    <a href="https://www.finclip.com?from=github">
    <img width="auto" src="https://www.finclip.com/mop/document/images/logo.png">
    </a>
</p>

<p align="center"> 
    <strong>FinClip Android DEMO</strong></br>
<p>
<p align="center"> 
        本项目提供在 Android 环境中接入凡泰定制的用于提供小程序部分微信SDK能力的示例
<p>

<p align="center"> 
	👉 <a href="https://www.finclip.com?from=github">https://www.finclip.com/</a> 👈
</p>

<div align="center">

<a href="#"><img src="https://img.shields.io/badge/%E4%B8%93%E5%B1%9E%E5%BC%80%E5%8F%91%E8%80%85-20000%2B-brightgreen"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%B7%B2%E4%B8%8A%E6%9E%B6%E5%B0%8F%E7%A8%8B%E5%BA%8F-6000%2B-blue"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%B7%B2%E9%9B%86%E6%88%90%E5%B0%8F%E7%A8%8B%E5%BA%8F%E5%BA%94%E7%94%A8-75%2B-yellow"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%AE%9E%E9%99%85%E8%A6%86%E7%9B%96%E7%94%A8%E6%88%B7-2500%20%E4%B8%87%2B-orange"></a>

<a href="https://www.zhihu.com/org/finchat"><img src="https://img.shields.io/badge/FinClip--lightgrey?logo=zhihu&style=social"></a>
<a href="https://www.finclip.com/blog/"><img src="https://img.shields.io/badge/FinClip%20Blog--lightgrey?logo=ghost&style=social"></a>



</div>

<p align="center">

<div align="center">

[官方网站](https://www.finclip.com/) | [示例小程序](https://www.finclip.com/#/market) | [开发文档](https://www.finclip.com/mop/document/) | [部署指南](https://www.finclip.com/mop/document/introduce/quickStart/cloud-server-deployment-guide.html) | [SDK 集成指南](https://www.finclip.com/mop/document/introduce/quickStart/intergration-guide.html) | [API 列表](https://www.finclip.com/mop/document/develop/api/overview.html) | [组件列表](https://www.finclip.com/mop/document/develop/component/overview.html) | [隐私承诺](https://www.finclip.com/mop/document/operate/safety.html)

</div>

-----
## 🤔 FinClip 是什么?

有没有**想过**，开发好的微信小程序能放在自己的 APP 里直接运行，只需要开发一次小程序，就能在不同的应用中打开它，是不是很不可思议？

有没有**试过**，在自己的 APP 中引入一个 SDK ，应用中不仅可以打开小程序，还能自定义小程序接口，修改小程序样式，是不是觉得更不可思议？

这就是 FinClip ，就是有这么多不可思议！

## 使用说明

在项目的`build.gradle`文件中（如`app/build.gradle`）添加您在微信开放平台申请的移动应用APPID：

```groovy
android {
    // ..其它配置省略
    defaultConfig {
    // ..其它配置省略
    resValue "string", "wechat_sdk_app_id", "您的微信开放平台移动应用下AppID"  
    }
}
```

> 这里填写的是移动应用下的AppID， 一般情况是wx开头，注意不是微信小程序的AppId，也不是微信小程序原始ID（gh开头），这些ID很容易搞混。

由于WeChatSDK需要覆盖`IAppletHandler`中的`open-type`相关的方法，具体为`chooseAvatar`、`contact`、`feedback`、`getPhoneNumber`、`launchApp`、`shareAppMessage`六个方法。

因此若您实现了`IAppletHandler`并实现了以上六个方法，WeChatSDK将会接管`getPhoneNumber`，剩余的五个方法请按以下方式迁移，若您未实现`IAppletHandler`或没有用到以上六个方法，可以忽略此处。

1. 实现`IWeChatOpenTypeHandler`接口：

   ```kotlin
   class MyWeChatAppletOpenTypeHandler : IWeChatOpenTypeHandler {
       override fun chooseAvatar(callback: IAppletHandler.IAppletCallback) {
          // 您的实现逻辑
       }
   
       override fun contact(json: JSONObject): Boolean {
          // 您的实现逻辑
       }
   
       override fun feedback(bundle: Bundle): Boolean {
          // 您的实现逻辑
       }
   
       override fun launchApp(appParameter: String?): Boolean {
          // 您的实现逻辑
       }
   
       override fun shareAppMessage(
           appInfo: String,
           bitmap: Bitmap?,
           callback: IAppletHandler.IAppletCallback
       ) {
          // 您的实现逻辑
       }
   }
   ```

2. 在核心SDK初始化成功后，设置您的实现类。（注意，同核心SDK一样，务必保证是在主进程中设置）：

   ```kotlin
   WeChatOpenTypeClient.instance.iWeChatOpenTypeHandler = MyWeChatAppletOpenTypeHandler()
   ```
