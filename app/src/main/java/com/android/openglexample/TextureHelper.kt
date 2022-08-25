package com.android.openglexample

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import androidx.annotation.DrawableRes

object TextureHelper {

    fun loadTexture(context: Context, @DrawableRes resID: Int): Int {
        val textureObjIds = IntArray(1)
        // 生成纹理对象，获取其 id
        GLES20.glGenTextures(1, textureObjIds, 0)

        if (textureObjIds[0] == 0) {
            return 0
        }

        val options = BitmapFactory.Options().apply {
            inScaled = false
        }

        val bitmap = BitmapFactory.decodeResource(context.resources, resID, options)
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureObjIds, 0)
            return 0
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjIds[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        bitmap.recycle()

        // 接触纹理绑定，防止其他逻辑意外影响到 textureObjIds[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureObjIds[0]
    }
}