package com.android.openglexample.obj

data class Point(val x: Float, val y: Float, val z: Float) {

    fun translateY(distance: Float): Point {
        return Point(this.x, this.y + distance, this.z)
    }
}

