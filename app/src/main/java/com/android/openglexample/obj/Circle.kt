package com.android.openglexample.obj

data class Circle(val center: Point, val radius: Float) {

    fun scale(scale: Float): Circle {
        return Circle(center, this.radius * scale)
    }
}