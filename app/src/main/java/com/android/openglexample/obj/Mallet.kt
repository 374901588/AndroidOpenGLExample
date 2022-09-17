package com.android.openglexample.obj

import com.android.openglexample.data.VertexArray
import com.android.openglexample.program.ColorShaderProgram

class Mallet(
    val radius: Float,
    val height: Float,
    numPointsAroundMallet: Int
) {

    companion object {
        const val POSITION_COMPONENT_COUNT = 3
    }

    private val vertexArray: VertexArray
    private val drawList: List<DrawCommand>

    init {
        val createMallet = ObjectBuilder.createMallet(
            Point(0f, 0f, 0f), radius, height, numPointsAroundMallet
        )

        vertexArray = VertexArray(createMallet.vertexData)
        drawList = createMallet.drawList
    }

    // 把顶点数据绑定到着色器程序定义的属性上
    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0, colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0
        )
    }

    fun draw() {
        drawList.forEach {
            it.draw()
        }
    }
}