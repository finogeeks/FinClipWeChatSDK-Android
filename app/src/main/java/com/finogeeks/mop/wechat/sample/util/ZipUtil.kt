package com.finogeeks.mop.wechat.sample.util

import android.util.Log
import java.io.*
import java.util.zip.ZipInputStream

private const val TAG = "ZipUtil"

/**
 * 解压zip文件
 *
 * @param inputPath zip文件路径
 * @param outputDir 解压输出路径
 * @return true：解压成功，否则亦然
 */
fun unzipFile(inputPath: String?, outputDir: String?): Boolean {
    return if (inputPath.isNullOrBlank() || outputDir.isNullOrBlank()) {
        false
    } else try {
        unzipFile(FileInputStream(inputPath), outputDir)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "unzip from file exception, " + e.message)
        false
    }
}

/**
 * 解压zip文件
 *
 * @param inputStream zip文件输入流
 * @param outputDir   解压输出路径
 * @return true：解压成功，否则亦然
 */
fun unzipFile(inputStream: InputStream?, outputDir: String?): Boolean {
    if (inputStream == null || outputDir.isNullOrBlank()) {
        return false
    }
    var zis: ZipInputStream? = null
    var fos: FileOutputStream? = null
    try {
        zis = ZipInputStream(inputStream)
        var zipEntry = zis.nextEntry
        val buffer = ByteArray(4096)
        while (zipEntry != null) {
            val fileName = zipEntry.name
            if (!fileName.isNullOrBlank()) {
                if (zipEntry.isDirectory) {
                    val newDir = File(outputDir, fileName)
                    Log.d(TAG, "unzip Dir: " + newDir.absolutePath)
                    newDir.mkdirs()
                } else {
                    try {
                        val newFile = File(outputDir, fileName)
                        Log.d(TAG, "unzip File: " + newFile.canonicalPath)
                        fos = FileOutputStream(newFile)
                        var len: Int
                        while (zis.read(buffer).also { len = it } > 0) {
                            fos.write(buffer, 0, len)
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } finally {
                        if (fos != null) {
                            fos.flush()
                            fos.close()
                        }
                    }
                }
            }
            zipEntry = zis.nextEntry
        }
        Log.d(TAG, "unzip done")
    } catch (e: IOException) {
        Log.e(TAG, "unzip from inputStream exception, " + e.message)
        return false
    } finally {
        closeAll(zis, fos)
    }
    return true
}