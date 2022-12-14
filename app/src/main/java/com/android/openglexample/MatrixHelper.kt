package com.android.openglexample

object MatrixHelper {

    @JvmStatic
    fun perspectiveM(m: FloatArray, yFovInDegress: Float, aspect: Float, n: Float, f: Float) {
        val angleInRadians: Float = (yFovInDegress * Math.PI / 2.0f).toFloat()
        val a: Float = (1.0 / Math.tan(angleInRadians / 2.0)).toFloat()

        m[0] = a / aspect
        m[1] = 0f
        m[2] = 0f
        m[3] = 0f

        m[4] = 0f
        m[5] = a
        m[6] = 0f
        m[7] = 0f

        m[8] = 0f
        m[9] = 0f
        m[10] = -((f + n) / (f - n))
        m[11] = -1f
        m[12] = 0f
        m[13] = 0f
        m[14] = -((2 * f * n) / (f - n))
        m[15] = 0f
    }
}