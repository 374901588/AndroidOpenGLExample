package com.android.openglexample.program

import android.content.Context
import android.opengl.GLES20
import com.android.openglexample.R

class ColorShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {

    private val uMatrixLocation: Int = GLES20.glGetUniformLocation(program, U_MATRIX)

    private val uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR)
    private val aPositionLocaltion = GLES20.glGetAttribLocation(program, A_POSITION)

    fun setUniforms(
        matrix: FloatArray,
        r: Float,
        g: Float,
        b: Float,
    ) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocaltion
    }
}