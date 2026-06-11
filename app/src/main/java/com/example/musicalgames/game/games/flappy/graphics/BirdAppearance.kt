package com.example.musicalgames.game.games.flappy.graphics

import android.graphics.Canvas
import android.graphics.RectF

interface BirdAppearance {
    fun draw(canvas: Canvas, bounds: RectF)
}
