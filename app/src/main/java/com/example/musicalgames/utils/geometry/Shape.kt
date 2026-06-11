package com.example.musicalgames.utils.geometry

interface Shape {
    fun intersects(rect: Rect): Boolean
    fun getTranslated(translationVector: Point): Shape
}