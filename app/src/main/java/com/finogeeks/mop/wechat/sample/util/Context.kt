package com.finogeeks.mop.wechat.sample.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.support.annotation.StringRes
import android.view.WindowManager

/**
 * 确保在UI线程中弹Toast
 */
fun Context.toastOnUiThread(@StringRes message: Int) {
    runOnUiThread { toast(message) }
}

/**
 * 确保在UI线程中弹Toast
 */
fun Context.toastOnUiThread(message: CharSequence) {
    runOnUiThread { toast(message) }
}

/**
 * 确保在UI线程中弹Toast
 */
fun Context.longToastOnUiThread(@StringRes message: Int) {
    runOnUiThread { longToast(message) }
}

/**
 * 确保在UI线程中弹Toast
 */
fun Context.longToastOnUiThread(message: CharSequence) {
    runOnUiThread { longToast(message) }
}

/**
 * 获取状态栏的高度
 */
fun Context.getStatusBarHeightInPixel(): Int {
    val resId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resId > 0) {
        return resources.getDimensionPixelSize(resId)
    }
    return 0
}

/**
 * 获取屏幕宽度
 */
fun Context.screenWidth() = resources.displayMetrics.widthPixels

/**
 *
 * 获取屏幕高度
 */
fun Context.screenHeight() = resources.displayMetrics.heightPixels

/**
 * 如果[Context]属于[Activity]，则为其指定具体的转场动画
 */
fun Context.overridePendingTransition(enterAnim: Int, exitAnim: Int) {
    (this as? Activity)?.overridePendingTransition(enterAnim, exitAnim)
}

/**
 * 获取屏幕方向
 */
fun Context.screenOrientation(): Int {
    return resources?.configuration?.orientation ?: Configuration.ORIENTATION_PORTRAIT
}

/**
 * 判断当前设备是否是全面屏设备
 */
fun Context.isFullScreenDevice(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        return false
    }
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return false
    val display = windowManager.defaultDisplay ?: return false
    val point = Point()
    display.getRealSize(point)
    val x = point.x.toFloat()
    val y = point.y.toFloat()
    if (x < 1 || y < 1) {
        return false
    }
    val width: Float
    val height: Float
    if (x < y) {
        width = x
        height = y
    } else {
        width = y
        height = x
    }
    if (height / width >= 2.0f) {
        return true
    }
    return false
}

fun Context.dp2pixels(dp: Int): Int {
    return (resources.displayMetrics.density * dp).toInt()
}