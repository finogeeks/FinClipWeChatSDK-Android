package com.finogeeks.mop.wechat.open_type_handler

import android.graphics.Bitmap
import android.os.Bundle
import com.finogeeks.lib.applet.sdk.api.IAppletHandler
import org.json.JSONObject

/**
 * 用于提供给宿主app处理除了 getPhoneNumber 之外的其它 open-type 方法
 */
interface IWeChatOpenTypeHandler {

    /**
     * 选择头像
     */
    fun chooseAvatar(callback: IAppletHandler.IAppletCallback)

    /**
     * 从小程序返回到主app
     */
    fun launchApp(appParameter: String?): Boolean

    fun contact(json: JSONObject): Boolean

    /**
     * 转发小程序
     *
     * @param appInfo 小程序信息，是一串json，包含了小程序id、小程序名称、小程序图标、用户id、转发的数据内容等信息。
     * [appInfo]的内容格式如下：
     * {
     *      "appTitle": "凡泰小程序",
     *      "appAvatar": "https:\/\/www.finogeeks.club\/statics\/images\/swan_mini\/swan_logo.png",
     *      "appId": "5df36b3f687c5c00013e9fd1",
     *      "appType": "trial",
     *      "userId": "finogeeks",
     *      "cryptInfo": "SFODj9IW1ENO8OA0El8P79aMuxB1DJvfKenZd7hrnemVCNcJ+Uj9PzkRkf/Pu5nMz0cGjj0Ne4fcchBRCmJO+As0XFqMrOclsqrXaogsaUPq2jJKCCao03vI8rkHilrWxSDdzopz1ifJCgFC9d6v29m9jU29wTxlHsQUtKsk/wz0BROa+aDGWh0rKvUEPgo8mB+40/zZFNsRZ0PjsQsi7GdLg8p4igKyRYtRgOxUq37wgDU4Ymn/yeXvOv7KrzUT",
     *      "params": {
     *           "title": "apt-test-tweet-接口测试发布的动态！@#￥%……&*（",
     *           "desc": "您身边的服务专家",
     *           "imageUrl": "finfile:\/\/tmp_fc15edd8-2ff6-4c54-9ee9-fe5ee034033d1576550313667.png",
     *           "path": "pages\/tweet\/tweet-detail.html?fcid=%40staff_staff1%3A000000.finogeeks.com&timelineId=db0c2098-031e-41c4-b9c6-87a5bbcf681d&shareId=3dfa2f78-19fc-42fc-b3a9-4779a6dac654",
     *           "appInfo": {
     *               "weixin": {
     *                   "path": "\/studio\/pages\/tweet\/tweet-detail",
     *                   "query": {
     *                       "fcid": "@staff_staff1:000000.finogeeks.com",
     *                       "timelineId": "db0c2098-031e-41c4-b9c6-87a5bbcf681d"
     *                    }
     *               }
     *           }
     *       }
     * }
     * [appInfo]中各字段的说明：
     * appId 小程序ID
     * appTitle 小程序名称
     * appAvatar 小程序头像
     * appType 小程序类型，其中trial表示体验版，temporary表示临时版，review表示审核版，release表示线上版，development表示开发版
     * userId 用户ID
     * cryptInfo 小程序加密信息
     * params 附带的其它参数，由小程序自己透传
     *
     * @param bitmap 小程序封面图片。如果[appInfo].params.imageUrl字段为http、https的链接地址，那么小程序封面图片
     * 就取[appInfo].params.imageUrl对应的图片，否则小程序的封面图片取[bitmap]。
     * @param callback 转发小程序结果回调。
     */
    fun shareAppMessage(appInfo: String, bitmap: Bitmap?, callback: IAppletHandler.IAppletCallback)

    /**
     * 打开宿主app用户反馈
     */
    fun feedback(bundle: Bundle): Boolean
}