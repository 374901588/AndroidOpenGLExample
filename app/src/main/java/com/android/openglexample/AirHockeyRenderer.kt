package com.android.openglexample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.android.openglexample.obj.Mallet
import com.android.openglexample.obj.Puck
import com.android.openglexample.obj.Table
import com.android.openglexample.program.ColorShaderProgram
import com.android.openglexample.program.TextureShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // 投影矩阵：帮助创建三维的幻象
    // 在本例中通过 MatrixHelper.perspectiveM() 来处理了
    private val projectionMatrix = FloatArray(16)
    // 模型矩阵：用来把物体放在世界空间坐标系中的
    private val modelMatrix = FloatArray(16)

    // 视图矩阵：处于同模型矩阵一样的原因被使用，但是会平等影响场景中的每一个物体。
    //          它的功能相当于：来回移动相机，从而从不同视角看见那些物体
    // 在本例子中通过 Matrix.setLookAtM() 来设置了
    private val viewMatrix = FloatArray(16)
    // 用于存放 投影矩阵*视图矩阵 的结果
    private val viewProjectionMatrix = FloatArray(16)
    // 用于 viewProjectionMatrix*模型矩阵 的结果
    private val modelViewProjectionMatrix = FloatArray(16)

    private lateinit var puck: Puck

    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspect = width * 1.0f / height
        MatrixHelper.perspectiveM(projectionMatrix, 45f, aspect, 1f, 10f)

        Matrix.setLookAtM(
            viewMatrix, 0,
            // 眼睛的位置在 x-z 平面上方 1.2 个单位，并向后 2.2 个单位，
            // 即场景中的所有东西都出现在你下面 1.2 个单位和前面 2.2 个单位的地方
            // 通过修改 eyeX、eyeY、eyeZ 可以实现练习中的绕着桌子来旋转人的视点，实现围绕从不同角度看桌子的效果
            0f, 1.2f, 2.2f,
            // 图像的中心位置，(0f, 0f, 0f) 即图像处于原点，
            //  (0f, 0.5f, 0f) 即图像处于原点往下 0.5 个单位的地方（即偏向下方的位置）
            0f, 0f, 0f,
            // 头对应的方向，比如竖直摆放 (0f, 1f, 0f)，也可以横平朝左摆放(1f, 0f, 0f)，不同的方向，看到的内容方向就不一样
            0f, 1f, 0f
        )
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 把投影和视图矩阵乘在一起的结果缓存到 viewProjectionMatrix 中
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        // 第一个木槌
        positionObjectInScene(0f, mallet.height / 2f, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        // 第二个木槌
        positionObjectInScene(0f, mallet.height / 2f, 0.4f)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.bindData(colorProgram)
        mallet.draw()

        // 冰球
        positionObjectInScene(0f, puck.height / 2f, 0f)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()
    }

    private fun positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0)
        // 桌子原来是以 x、y 坐标定义的，因此要使它平放在地方，需要绕 x 轴向后旋转 90 度
        // 不需要像之前那样把桌子平移，因为我们想让桌子在世界坐标中保持在 (0, 0) 的位置，并且视图矩阵已经想办法使桌子对我们可见了
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)

        // viewProjectionMatrix 存放了 投影矩阵*视图矩阵 的结果
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    // 木槌和冰球已经被定义了，并且被水平方在 x-z 平面上了，因此不需要再旋转，只需要平移到指定的位置
    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }
}