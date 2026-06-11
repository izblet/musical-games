package com.example.musicalgames.game.games.flappy.game_logic

import android.graphics.Rect

class Bird (private val shape: Shape){
    fun moveByVector(x:Double, y: Double) {

    }
    fun intersects(rectangle: Rect): Boolean {
        return shape.intersects(rectangle)
    }
}