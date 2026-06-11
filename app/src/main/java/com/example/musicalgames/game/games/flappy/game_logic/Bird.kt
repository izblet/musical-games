package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.utils.geometry.Point
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

class Bird (shape: Shape, private val moveSpeedDiv: Int, translation : Point = Point(.0,.0)){

    private var shape = shape.getTranslated(translation)
    private var constraint: Rect? = null

    private var target = shape.getCenter().y


    fun setConstraints(rect: Rect) {
        require(shape.isContained(rect)) { "Bird's shape is not contained within the given constraints" }
        constraint = rect
    }
    fun intersects(rectangle: Rect): Boolean {
        return getShape().intersects(rectangle)
    }
    fun getShape(): Shape {
        return shape.copy()
    }

    fun move(newTarget: Double) {
        target = newTarget
        val deltaY = (target - shape.getCenter().y) / moveSpeedDiv
        val translatedShape = shape.getTranslated(Point(.0, deltaY))

        val withinBounds = constraint?.let { translatedShape.isContained(it) } ?: true

        if (withinBounds) {
            shape = translatedShape
        }
    }
}