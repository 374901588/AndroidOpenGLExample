package com.android.openglexample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.android.openglexample.obj.Mallet
import com.android.openglexample.obj.Table
import com.android.openglexample.program.ColorShaderProgram
import com.android.openglexample.program.TextureShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// TODO 无法正常运行
class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet()

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspect = width * 1.0f / height
        MatrixHelper.perspectiveM(projectionMatrix, 45f, aspect, 1f, 10f)

        // 设置为单位矩阵
        Matrix.setIdentityM(modelMatrix, 0)
        // 再沿着 z 轴平移 -2
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        // 沿 x 轴旋转 -60 度
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val tmp = FloatArray(16)
        Matrix.multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.size)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        textureProgram.useProgram()
        textureProgram.setUniforms(projectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        colorProgram.useProgram()
        colorProgram.setUniforms(projectionMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }
}