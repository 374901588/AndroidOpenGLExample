package com.android.openglexample

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        glSurfaceView = findViewById(R.id.glsv)
        // 要记得调用，不然在调用 glCreateShader 的时候会出现
        // "Fatal signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x0 in tid 23136 (GLThread 28975)"
        glSurfaceView.setEGLContextClientVersion(2)
        val renderer = AirHockeyRenderer(this)
        glSurfaceView.setRenderer(renderer)

        glSurfaceView.setOnTouchListener { v, event ->
            event ?: false

            // 获取归一化坐标 z
            // event.x / v.width，还只是将坐标点基于 view.width 射到 [0, 1] 的范围，
            // 但是归一化设备坐标是 [-1, 1]，所以需要 *2，即 [0, 1] -> [0, 2]，
            // 然后 -1，即 [0, 2] -> [-1, 1]
            val normalizedX = (event.x * 1.0f / v.width) * 2 - 1
            // y 轴需要反转，获得归一化坐标 y
            val normalizedY = -((event.y * 1.0f / v.height) * 2 - 1)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    glSurfaceView.queueEvent {
                        renderer.handleTouchPress(normalizedX, normalizedY)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    glSurfaceView.queueEvent {
                        renderer.handleTouchDrag(normalizedX, normalizedY)
                    }
                }
            }

            true
        }
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
}