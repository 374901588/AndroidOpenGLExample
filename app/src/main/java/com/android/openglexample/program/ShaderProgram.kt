package com.android.openglexample.program

import android.content.Context
import android.opengl.GLES20
import com.android.openglexample.ShaderHelper
import com.android.openglexample.readTextFileFromRes

open class ShaderProgram(context: Context, vertextShaderResId: Int, fragShaderResId: Int) {

    companion object {
        const val U_MATRIX = "u_Matrix"
        const val U_TEXTURE_UNIT = "u_TextureUnit"

        const val A_POSITION = "a_Position"
        const val A_COLOR = "a_Color"

        const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }

    // 链接程序，获取对应的 id
    protected val program: Int = ShaderHelper.buildProgram(
        readTextFileFromRes(context, vertextShaderResId),
        readTextFileFromRes(context, fragShaderResId)
    )

    fun useProgram() {
        GLES20.glUseProgram(program)
    }

}