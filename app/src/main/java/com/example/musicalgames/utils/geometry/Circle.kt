package com.example.musicalgames.utils.geometry

class Circle(val center: Point, val radius: Double) : Shape {
    override fun intersects(rect: Rect) : Boolean {
        val closestX = center.x.coerceIn(rect.left, rect.right)
        val closestY = center.y.coerceIn(rect.bottom, rect.top)
        val dx = center.x - closestX
        val dy = center.y - closestY
        return dx * dx + dy * dy <= radius * radius
    }

    override fun getTranslated(translationVector: Point): Circle {
        return Circle(center.getTranslated(translationVector), radius)
    }
}