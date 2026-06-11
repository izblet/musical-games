package com.example.musicalgames.utils.geometry

interface Shape {
    fun intersects(rect: Rect): Boolean
    fun getTranslated(translationVector: Point): Shape
    fun isContained(rect: Rect): Boolean
    fun getCenter(): Point
    fun copy(): Shape
    fun getBoundingRectangle(): Rect
}