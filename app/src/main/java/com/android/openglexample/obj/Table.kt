package com.android.openglexample.obj

import android.opengl.GLES20
import com.android.openglexample.Constants
import com.android.openglexample.data.VertexArray
import com.android.openglexample.program.TextureShaderProgram

class Table {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PRE_FLOAT
    }

    private val VERTEX_DATA = floatArrayOf(
        // order of coordinates: X、Y、S、T

        0f,     0f,     0.5f,       0.5f,
        -0.5f,  -0.8f,   0f,        0.9f,
        0.5f,  -0.8f,   1f,        0.9f,
        0.5f,  0.8f,   1f,        0.1f,
        -0.5f,  0.8f,   0f,        0.1f,
        -0.5f,  -0.8f,   0f,        0.9f
    )

    private val vertextArr: VertexArray = VertexArray(VERTEX_DATA)

    fun bindData(textureProgram: TextureShaderProgram) {
        vertextArr.setVertexAttribPointer(
            0,
            textureProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        vertextArr.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            textureProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }
}