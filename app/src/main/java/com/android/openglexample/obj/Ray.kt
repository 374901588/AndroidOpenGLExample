package com.android.openglexample.obj

data class Ray(val point: Point, val vector: Vector)

data class Vector(val x: Float, val y: Float, val z: Float) {

    fun length(): Float {
        return Math.sqrt(
            (x * x + y * y + z * z).toDouble()
        ).toFloat()
    }

    /**
     * 获得两个向量的交叉乘积
     */
    fun crossProduct(other: Vector): Vector {
        return Vector(
            (y * other.z) - (z * other.y),
            (z * other.x) - (x * other.z),
            (x * other.y) - (y * other.x),
        )
    }

    /**
     * 计算两个向量之间的点积
     */
    fun dotProduct(other: Vector): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun scale(f: Float): Vector {
        return Vector(x * f, y * f, z * f)
    }
}

data class Sphere(val center: Point, val radius: Float)

data class Plane(val point: Point,
                 /**
                  * 法向向量
                  * 三维平面的法线是垂直于该平面的三维向量
                  */
                 val normal: Vector)

/**
 * 得到两个点对应的向量
 */
fun vectorBetween(from: Point, to: Point): Vector {
    return Vector(
        to.x - from.x,
        to.y - from.y,
        to.z - from.z,
    )
}

/**
 * 判断射线与球体是否相交
 */
fun intersects(sphere: Sphere, ray: Ray): Boolean {
    val distance = distanceBetween(sphere.center, ray)
    return distance <= sphere.radius
}

fun distanceBetween(point: Point, ray: Ray): Float {
    // 第一个向量：从射线的第一个点到球心
    val p1ToPoint = vectorBetween(ray.point, point)
    // 第二个向量：从射线的第二个点到球心
    val p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point)

    // 上述两个向量一起定义了射线与球心形成的三角形

    // 上述两个向量的交叉乘积，是一个新的向量
    // 该向量的长度，即上述三角形面积的 2 倍
    val vectorProduct = p1ToPoint.crossProduct(p2ToPoint)
    val areaOfTriangleTimesTwo = vectorProduct.length()

    // 而对于一个三角形来说，(底*高)/2 即三角形的面积
    // 即高 = 面积 * 2 / 底
    // 这里的高即射线到圆心的距离，底即射线的长度
    val lenOfBase = ray.vector.length()

    val disFromPointToRay = areaOfTriangleTimesTwo / lenOfBase

    return disFromPointToRay
}

// 得到射线与平面的相交点
fun intersectsPoint(ray: Ray, plane: Plane): Point {
    val rayToPlaneVector = vectorBetween(ray.point, plane.point)

    // 缩放因子，代表射线的向量要缩放多少才能刚好与平面相接触
    val scaleFcator =
        rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal)

    val intersectsPoint = ray.point.translate(ray.vector.scale(scaleFcator))

    return intersectsPoint
}

