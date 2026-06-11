package com.example.musicalgames.utils.geometry

class Circle(val center: Point, val radius: Double) : Shape {
    init {
        require(radius>0) {"Circle radius has to be positive"}
    }
    override fun intersects(rect: Rect) : Boolean {
        val closestX = center.x.coerceIn(rect.left, rect.right)
        val closestY = center.y.coerceIn(rect.bottom, rect.top)
        val dx = center.x - closestX
        val dy = center.y - closestY
        return dx * dx + dy * dy <= radius * radius
    }

    override fun isContained(rect: Rect): Boolean {
        return center.x - radius >= rect.left &&
                center.x + radius <= rect.right &&
                center.y - radius >= rect.bottom &&
                center.y + radius <= rect.top
    }

    override fun getTranslated(translationVector: Point): Circle {
        return Circle(center.getTranslated(translationVector), radius)
    }

    override fun getCenter(): Point {
        return center
    }

    override fun copy(): Circle {
        return Circle(center, radius)
    }

    override fun getBoundingRectangle(): Rect {
        return Rect(
            left = center.x - radius,
            top = center.y + radius,
            right = center.x + radius,
            bottom = center.y - radius
        )
    }
}