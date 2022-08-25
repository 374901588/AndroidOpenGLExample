package com.android.openglexample

object ShaderHelper {

    fun buildProgram(vertexShaderSrc: String, fragShaderSrc: String): Int {
        val vertexShader = compileVertexShader(vertexShaderSrc)
        val fragShader = compileFragmentShader(fragShaderSrc)

        return linkProgram(vertexShader, fragShader)
    }
}