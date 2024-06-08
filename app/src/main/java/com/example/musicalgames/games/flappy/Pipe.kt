package com.example.musicalgames.games.flappy


class Pipe(
    var x: Int,
    var topHeight: Int,
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
