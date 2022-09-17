package com.android.openglexample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.android.openglexample.obj.*
import com.android.openglexample.program.ColorShaderProgram
import com.android.openglexample.program.TextureShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// TODO 目前实现的用蓝木槌碰撞冰球，但是冰球再碰撞之后移动的时候，
//  如果遇到了木槌，并不会反向再碰撞木槌而使木槌被动移动，因为这部分还没有实现
class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val leftBound = -0.5f
    private val rightBound = 0.5f

    private val farBound = -0.8f
    private val nearBound = 0.8f

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

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return Math.min(max, Math.max(value, min))
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)

        blueMalletPos = Point(0f, mallet.height / 2f, 0.4f)

        puck = Puck(0.06f, 0.02f, 32)

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // 初始化冰球的数据
        puckPos = Point(0f, puck.height / 2f, 0f)
        puckVector = Vector(0f, 0f, 0f)

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

        // 获得反转矩阵
        Matrix.invertM(invertedProjectionMatrix, 0, viewProjectionMatrix, 0)

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
        positionObjectInScene(blueMalletPos)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.bindData(colorProgram)
        mallet.draw()

        drawPuck()
    }

    private fun drawPuck() {
        // 冰球被碰撞之后的看距离
        puckPos = puckPos.translate(puckVector)

        // 边界检查

        // 检查冰球是否越过了桌子的两边，否则反转移动的方向
        if (puckPos.x < leftBound + puck.radius ||
            puckPos.x > rightBound - puck.radius
        ) {
            puckVector = Vector(-puckVector.x, puckVector.y, puckVector.z)
        }

        // 然后检查冰球是否越过桌子的近边或者远边
        // 注意，负的 z 值表示距离，东西离得越远，z 值越小
        if (puckPos.z < farBound + puck.radius ||
            puckPos.z > nearBound - puck.radius
        ) {
            puckVector = Vector(puckVector.x, puckVector.y, -puckVector.z)
        }

        puckPos = Point(
            clamp(
                puckPos.x,
                leftBound + puck.radius,
                rightBound - puck.radius
            ),
            puckPos.y,
            clamp(
                puckPos.z,
                farBound + puck.radius,
                nearBound - puck.radius
            )
        )

        // 冰球
        positionObjectInScene(puckPos.x, puckPos.y, puckPos.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()

        // 模拟阻尼，使冰球的移动向量长度不断变小
        puckVector = puckVector.scale(0.99f)
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

    private fun positionObjectInScene(point: Point) {
        positionObjectInScene(point.x, point.y, point.z)
    }

    // region 触控反馈

    private lateinit var puckPos: Point
    private lateinit var puckVector: Vector

    private var malletPressed = false
    private lateinit var blueMalletPos: Point
    private lateinit var preBlueMalletPos: Point

    fun handleTouchPress(x: Float, y: Float) {
        // 假设木槌被一个相当大小的球包围
        // 简化射线与木槌是否相交的判断，从而只需要判断射线是否与球体相交
        // 即判断射线到球心的距离，是否 <= 球体半径
        val malletBoundingSphere = Sphere(
            Point(
                blueMalletPos.x,
                blueMalletPos.y,
                blueMalletPos.z
            ),
            mallet.height / 2f
        )

        val ray = convertNormalized2DPointToRay(x, y)

        malletPressed = intersects(malletBoundingSphere, ray)

    }

    fun handleTouchDrag(x: Float, y: Float) {
        if (malletPressed) {
            val ray = convertNormalized2DPointToRay(x, y)
            // 定义一个平面来代表桌面
            val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))

            val touchedPoint = intersectsPoint(ray, plane)

            preBlueMalletPos = blueMalletPos
            blueMalletPos = Point(
                clamp(
                    touchedPoint.x,
                    leftBound + mallet.radius,
                    rightBound - mallet.radius
                ),
                mallet.height / 2f,
                clamp(
                    touchedPoint.z,
                    // 让蓝木槌不跨越中线
                    0f + mallet.radius,
                    nearBound - mallet.radius
                ),
            )

            // 木槌移动之后，与冰球的距离
            val distance = vectorBetween(blueMalletPos, puckPos).length()
            // 如果这个距离小于 puck.radius + mallet.radius，则表示两者碰到了
            if (distance < (puck.radius + mallet.radius)) {
                // 蓝木槌前后的位置，获取冰球移动的向量
                puckVector = vectorBetween(preBlueMalletPos, blueMalletPos)
            }
        }
    }

    // 反转矩阵，取消视图矩阵和投影矩阵的效果，
    // 把被触碰点的转换为一个三维射线，射线与场景内物体相交的点，
    // 即对应到触碰到的物体
    private val invertedProjectionMatrix = FloatArray(16)

    private fun convertNormalized2DPointToRay(x: Float, y: Float): Ray {
        // (x, y, z, w)
        val nearPointNdc = floatArrayOf(x, y, -1f, 1f)
        val farPointNdc = floatArrayOf(x, y, 1f, 1f)


        val nearPointWord = FloatArray(4)
        val farPointWord = FloatArray(4)

        // 得到在世界空间中的坐标，其中 w 是反转的
        Matrix.multiplyMV(nearPointWord, 0, invertedProjectionMatrix, 0, nearPointNdc, 0)
        Matrix.multiplyMV(farPointWord, 0, invertedProjectionMatrix, 0, farPointNdc, 0)

        // 把 x、y、z 除以反转的 w，以撤销透视除法的影响
        // 最终把被触碰的屏幕上的点，转换为世界空间中远端、近端的两个点
        // 从而可以进一步获取到对应的射线
        devideByW(nearPointWord)
        devideByW(farPointWord)

        val nearPointRay = Point(nearPointWord[0], nearPointWord[1], nearPointWord[2])
        val farPointRay = Point(farPointWord[0], farPointWord[1], farPointWord[2])

        return Ray(nearPointRay, vectorBetween(nearPointRay, farPointRay))
    }

    private fun devideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    // endregion 触控反馈

}