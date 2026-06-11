package com.example.musicalgames.utils.geometry

class Circle(val centerPoint: Point, val radius: Double) : Shape {
    init {
        require(radius>0) {"Circle radius has to be positive"}
    }
    override fun intersects(rect: Rect) : Boolean {
        val closestX = centerPoint.x.coerceIn(rect.left, rect.right)
        val closestY = centerPoint.y.coerceIn(rect.bottom, rect.top)
        val dx = centerPoint.x - closestX
        val dy = centerPoint.y - closestY
        return dx * dx + dy * dy <= radius * radius
    }

    override fun isContained(rect: Rect): Boolean {
        return centerPoint.x - radius >= rect.left &&
                centerPoint.x + radius <= rect.right &&
                centerPoint.y - radius >= rect.bottom &&
                centerPoint.y + radius <= rect.top
    }

    override fun getTranslated(translationVector: Point): Circle {
        return Circle(centerPoint.getTranslated(translationVector), radius)
    }

    override fun getCenter(): Point {
        return centerPoint
    }

    override fun copy(): Circle {
        return Circle(centerPoint, radius)
    }

    override fun getBoundingRectangle(): Rect {
        return Rect(
            left = centerPoint.x - radius,
            top = centerPoint.y + radius,
            right = centerPoint.x + radius,
            bottom = centerPoint.y - radius
        )
    }
}