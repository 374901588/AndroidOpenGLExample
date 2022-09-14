package com.android.openglexample.obj

import android.opengl.GLES20

class ObjectBuilder {

    companion object {
        const val FLOATS_PER_VERTEX = 3

        // 计算圆柱体顶部的顶点数量
        // 顶部即用三角扇形构造的圆，有一个顶点在圆心，
        // 围着圆的每个点都是一个顶点
        // 并且围着圆的第一个顶点要重复两次才能使圆闭合
        fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        // 圆柱体侧面是一个卷起来的长方形，由一个三角形带构造，
        // 围着顶部圆的每个点都需要两个顶点，
        // 并且前两个顶点需要重复两次才能使这个管闭合
        fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }

        fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)

            val builder = ObjectBuilder(size)

            val puckTop = Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius
            )

            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCyliner(puck, numPoints)

            return builder.build()
        }

        fun createMallet(center: Point, radius: Float, height: Float, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(numPoints) * 2 +
                    sizeOfOpenCylinderInVertices(numPoints) * 2

            val builder = ObjectBuilder(size)

            val baseHeight = height * 0.25f

            val baseCircle = Circle(center.translateY(-baseHeight), radius)

            val baseCylinder = Cylinder(
                baseCircle.center.translateY(-baseHeight / 2),
                radius, baseHeight
            )

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCyliner(baseCylinder, numPoints)

            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f

            val handleCircle = Circle(center.translateY(height * 0.5f), handleRadius)

            val handleCylinder = Cylinder(
                handleCircle.center.translateY(-handleHeight / 2),
                handleRadius, handleHeight
            )

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCyliner(handleCylinder, numPoints)

            return builder.build()
        }
    }

    private fun build(): GeneratedData {
        return GeneratedData(vertexData, drawList)
    }

    private fun appendOpenCyliner(cylinder: Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfOpenCylinderInVertices(numPoints)

        val hHalf = cylinder.height / 2f
        val yStart = cylinder.center.y - hHalf
        val yEnd = cylinder.center.y + hHalf

        // PI 对应于 180 度
        val angle360 = Math.PI * 2f
        // 最开始的顶点需要重复一次，才能构成闭合的圆柱
        for (i in 0..numPoints) {
            val angleInRadians = ((i * 1.0 / numPoints) * angle360)

            val xPos = cylinder.center.x + (cylinder.radius * Math.cos(angleInRadians)).toFloat()
            val zPos = cylinder.center.z + (cylinder.radius * Math.sin(angleInRadians)).toFloat()

            // 圆柱上面的顶点
            vertexData[offset++] = xPos
            vertexData[offset++] = yStart
            vertexData[offset++] = zPos

            // 圆柱下面的顶点
            vertexData[offset++] = xPos
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPos
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                // GL_TRIANGLE_STRIP 对应三角带
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }

    // 用三角形扇构造圆
    private fun appendCircle(circle: Circle, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfCircleInVertices(numPoints)

        // 圆心，对应于多个扇形共用的一个顶点
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        // PI 对应于 180 度
        val angle360 = Math.PI * 2f

        // 最开始的顶点需要重复一次，才能构成闭合的圆
        for (i in 0..numPoints) {

            // 将一个圆，即 360 根据 numPoints 来均分，从而获取对应的三角形顶点
            val angleInRadians = ((i * 1.0 / numPoints) * angle360)

            vertexData[offset++] =
                circle.center.x + (circle.radius * Math.cos(angleInRadians)).toFloat()
            vertexData[offset++] = circle.center.y
            vertexData[offset++] =
                circle.center.z + (circle.radius * Math.sin(angleInRadians)).toFloat()
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                // GL_TRIANGLE_FAN 对应三角扇
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }

    private val vertexData: FloatArray
    private val drawList = mutableListOf<DrawCommand>()

    private var offset = 0

    private constructor(sizeInVertices: Int) {
        vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    }
}

data class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>)