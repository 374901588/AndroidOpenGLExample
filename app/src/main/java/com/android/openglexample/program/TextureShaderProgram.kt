package com.android.openglexample.program

import android.content.Context
import android.opengl.GLES20
import com.android.openglexample.R

class TextureShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader) {

    private val uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)
    private val uTextureLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT)

    private val aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
    private val aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    fun setUniforms(matrix: FloatArray, textureId: Int) {
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