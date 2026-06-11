package com.example.musicalgames.game.games.flappy.graphics

import android.graphics.Canvas
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

interface FlappyRenderer {
    fun draw(
        canvas: Canvas,
        birdShape: Shape,
        pipes: List<Triple<Rect, Rect?, Rect?>>,
        pipeNotes: List<Int>,
        gameRect: Rect,
        screenWidth: Float,
        screenHeight: Float
    )
}
