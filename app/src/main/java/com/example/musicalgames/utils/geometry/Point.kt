package com.example.musicalgames.utils.geometry

data class Point(val x: Double, val y: Double) {
    fun getTranslated(translationVector: Point) : Point{
        return Point(this.x + translationVector.x, this.y + translationVector.y)
    }
}
