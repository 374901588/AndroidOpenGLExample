package com.android.openglexample.program

import android.content.Context
import android.opengl.GLES20
import com.android.openglexample.R

class TextureShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader) {

    // 获取 texture_vertex_shader.glsl 中定义的变量 u_Matrix
    private val uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)
    // 获取 texture_fragment_shader.glsl 中定义的变量 u_TextureUnit
    private val uTextureLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT)

    // 获取 texture_vertex_shader.glsl 中定义的变量 a_Position
    private val aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
    // 获取 texture_vertex_shader.glsl 中定义的变量 a_TextureCoordinates
    private val aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        // 将外部传入的矩阵设置给 u_Matrix
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTextureLocation, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getTextureCoordinatesAttributeLocation(): Int {
        return aTextureCoordinatesLocation
    }
}