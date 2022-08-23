package com.finogeeks.mop.wechat.sample.util

import java.io.File

/**
 * 删除文件，如果是目录的话则会递归删除
 *
 * @param path 文件路径
 */
fun deleteFile(path: String?) {
    if (path.isNullOrBlank()) {
        return
    }
    val file = File(path)
    if (!file.exists()) {
        return
    }
    if (file.isFile) {
        file.delete()
        return
    }
    var files = file.listFiles()
    if (files.isNullOrEmpty()) {
        file.delete()
        return
    }
    for (f in files) {
        if (f.isFile) {
            f.delete()
        } else {
            deleteFile(f.absolutePath)
        }
    }
    files = file.listFiles()
    if (files.isNullOrEmpty()) {
        file.delete()
    }
}