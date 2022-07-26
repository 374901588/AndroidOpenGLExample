package com.android.openglexample

import android.content.Context
import android.opengl.GLES32.*
import android.util.Log
import java.io.*

fun readTextFileFromRes(context: Context, resId: Int): String {
    val sb = StringBuilder()
    var inputStream: InputStream? = null
    var inputStreamReader: InputStreamReader? = null
    var bufferedReader: BufferedReader? = null
    try {
        inputStream = context.resources.openRawResource(resId)
        inputStreamReader = InputStreamReader(inputStream)
        bufferedReader = BufferedReader(inputStreamReader)
        var nextLine = bufferedReader.readLine()
        while (nextLine != null) {
            sb.append("$nextLine\n")
            nextLine = bufferedReader.readLine()
        }
    } catch (t: Throwable) {

    } finally {
        bufferedReader?.safelyClose()
        inputStreamReader?.safelyClose()
        inputStream?.safelyClose()
        return sb.toString()
    }
}

fun Reader.safelyClose() {
    try {
        this.close()
    } catch (t: Throwable) {
    }
}

fun Closeable.safelyClose() {
    try {
        this.close()
    } catch (t: Throwable) {
    }
}