package com.android.openglexample

import android.opengl.GLES20
import android.opengl.GLES32
import android.util.Log

private fun printGlError(tag: String) {
    Log.e(tag, "printGlError: ${GLES20.glGetError()}")
}

fun compileVertexShader(shaderCode: String): Int {
    return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
}

fun compileFragmentShader(shaderCode: String): Int {
    return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
}

private fun compileShader(type: Int, code: String): Int {
    Log.d("compileShader", "start type:$type")
    val defErrCode = 0

    printGlError("compileShader1")
    val shaderObjId = GLES20.glCreateShader(type)
    printGlError("compileShader2")
    Log.e("compileShader", "shaderObjId: $shaderObjId")
    if (shaderObjId == 0) {
        return defErrCode
    }
    GLES20.glShaderSource(shaderObjId, code)
    GLES20.glCompileShader(shaderObjId)

    Log.e("compileShader", "$shaderObjId -> ${GLES20.glGetShaderInfoLog(shaderObjId)}")

    if (getCompileStatus(shaderObjId) == 0) {
        GLES20.glDeleteShader(shaderObjId)
        Log.e("compileShader", "compilation of shader failed")
        return defErrCode
    }

    return shaderObjId
}

private fun getCompileStatus(shaderObjId: Int): Int {
    val compileStatus = IntArray(1)
    GLES20.glGetShaderiv(shaderObjId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
    return compileStatus[0]
}

private fun getProgramLinkStatus(programId: Int): Int {
    val status = IntArray(1)
    GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, status, 0)
    return status[0]
}

fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
    val programId = GLES20.glCreateProgram()
    if (programId == 0) {
        Log.e("linkProgram", "couldn't create new program")
        return 0
    }

    GLES20.glAttachShader(programId, vertexShaderId)
    GLES20.glAttachShader(programId, fragmentShaderId)

    GLES20.glLinkProgram(programId)

    val programLinkStatus = getProgramLinkStatus(programId)
    if (programLinkStatus == 0) {
        GLES20.glDeleteProgram(programId)
        Log.e("compileShader", "linking of program failed")
        return 0
    }

    return programId
}

fun validateProgram(programId: Int): Boolean {
    GLES20.glValidateProgram(programId)
    val result = getProgramValidStatus(programId) != 0
    Log.d("validateProgram", "programId:$programId validate program result:$result")
    return result
}

private fun getProgramValidStatus(programId: Int): Int {
    val status = IntArray(1)
    GLES20.glGetProgramiv(programId, GLES20.GL_VALIDATE_STATUS, status, 0)
    return status[0]
}