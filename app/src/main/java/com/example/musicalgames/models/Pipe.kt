package com.example.musicalgames.models

class Bird(var x: Int, var y: Int, val width: Int, val height: Int)

class Pipe(
    var x: Int,
    var topHeight: Int,
    var bottomHeight: Int,
    var gap: Int
) {
    companion object {
        const val WIDTH = 100
        const val SPEED = 5
    }

    val bottomY: Int = topHeight + gap

    fun move() {
        x -= SPEED
    }

    fun isVisible(screenWidth: Int): Boolean {
        return x + WIDTH > 0 && x < screenWidth
    }
}
