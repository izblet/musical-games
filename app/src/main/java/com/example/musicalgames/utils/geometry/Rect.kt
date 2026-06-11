package com.example.musicalgames.utils.geometry

import kotlin.math.max
import kotlin.math.min


data class Rect (val left: Double, val top: Double, val right: Double, val bottom: Double) : Shape {

    override fun intersects(rect: Rect): Boolean {
        val xOverlap = left < rect.right && rect.left < right
        val yOverlap = bottom < rect.top && rect.bottom < top
        return xOverlap && yOverlap
    }

    override fun getTranslated(translationVector: Point): Rect {
        return Rect(
            left = left + translationVector.x,
            top = top + translationVector.y,
            right = right + translationVector.x,
            bottom = bottom + translationVector.y
        )
    }

    companion object {
        fun fromCorner(
            coordinate: Point,
            width: Double,
            height: Double,
            left: Boolean = true,
            bottom: Boolean = true
        ) : Rect {
            //how to multiply the width/height translation to get to the diagonal point
            val diagonalSignHorizontal = if (left) 1 else -1
            val diagonalSignVertical = if (bottom) 1 else -1

            //calculate the translation
            val diagonalTranslation =
                Point(width * diagonalSignHorizontal, height * diagonalSignVertical)

            //calculate second corner
            val diagonalPoint = coordinate.getTranslated(diagonalTranslation)
            return Rect(
                left = min(coordinate.x, diagonalPoint.x),
                top = max(coordinate.y, diagonalPoint.y),
                right = max(coordinate.x, diagonalPoint.x),
                bottom = min(coordinate.y, diagonalPoint.y)
            )
        }
    }

}