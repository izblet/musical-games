package com.example.musicalgames.game.games.flappy.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class CircleBirdAppearance(color: Int = Color.RED) : BirdAppearance {

    private val paint = Paint().apply { this.color = color }

    override fun draw(canvas: Canvas, bounds: RectF) {
        canvas.drawOval(bounds, paint)
    }
}
