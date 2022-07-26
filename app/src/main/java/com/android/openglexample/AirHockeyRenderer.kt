package com.android.openglexample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    companion object {
        private const val POSITION_COMPNENT_COUNT = 2

        private const val BYTES_PRE_FLOAT = 4

        private const val U_COLOR = "u_Color"

        private const val A_POSITION = "a_Position"
    }

    // 不管是 x 轴还是 y 轴，OpenGL 会把屏幕映射到 [-1, 1] 的范围
    private val tableVerticesWithTriangles: FloatArray = floatArrayOf(
        // triangles 1
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        // triangles 2
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        // line 1
        -0.5f, 0f,
        0.5f, 0f,

        // mallets 2
        0f, -0.25f,
        0f, 0.25f,

        // 处于中间的冰球
        0.0f, 0.0f
    )

    private var uColorLocaltion = 0
    private var aPositionLocaltion = 0

    private val vertexData: FloatBuffer

    init {
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PRE_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        vertexData.put(tableVerticesWithTriangles)
    }

    private var programId = 0
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        val vertexShaderSource = readTextFileFromRes(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource = readTextFileFromRes(context, R.raw.simple_fragment_shader)

        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)

        programId = linkProgram(vertexShader, fragmentShader)

        validateProgram(programId)

        GLES20.glUseProgram(programId)

        uColorLocaltion = GLES20.glGetUniformLocation(programId, U_COLOR)
        aPositionLocaltion = GLES20.glGetAttribLocation(programId, A_POSITION)

        vertexData.position(0)
        GLES20.glVertexAttribPointer(
            aPositionLocaltion,
            // 设置每个属性的数据的计数，或者对于这个属性，有多少个分量与每一个顶点相关联
            // 因为当前场景只用 x、y 两个坐标元素来表示，所以只需要 2 个
            POSITION_COMPNENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexData
        )

        GLES20.glEnableVertexAttribArray(aPositionLocaltion)

    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 绘制桌子
        GLES20.glUniform4f(uColorLocaltion, 1.0f, 1.0f, 1.0f, 1.0f)
        // 由于前面设置了 POSITION_COMPNENT_COUNT，所以这里读取 6 个，
        // 实际上是读取的 tableVerticesWithTriangles 中前 6 组数据，即对应 2 个三角形的 6 组坐标点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        // 绘制分割线
        GLES20.glUniform4f(uColorLocaltion, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        // 绘制蓝色木槌
        GLES20.glUniform4f(uColorLocaltion, 0.0f, 0.0f, 1.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

        // 绘制红色木槌
        GLES20.glUniform4f(uColorLocaltion, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)

        // 绘制处于中间的冰球
        // TODO 如何单独设置冰球的大小？？？
        GLES20.glUniform4f(uColorLocaltion, 0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 10, 1)
    }
}