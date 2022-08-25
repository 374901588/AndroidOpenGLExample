package com.android.openglexample.obj

import android.opengl.GLES20
import com.android.openglexample.Constants
import com.android.openglexample.data.VertexArray
import com.android.openglexample.program.ColorShaderProgram

class Mallet {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constants.BYTES_PRE_FLOAT
    }

    private val VERTEX_DATA = floatArrayOf(
        0f,     -0.4f,      0f,     0f,     1f,
        0f,      0.4f,      1f,     0f,     0f
    )

    private val vertexArr = VertexArray(VERTEX_DATA)

    fun bindData(colorPragram: ColorShaderProgram) {
        vertexArr.setVertexAttribPointer(
            0,
            colorPragram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        vertexArr.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorPragram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
    }
}