package com.example.musicalgames.game.games.flappy.graphics

import android.graphics.Canvas
import android.graphics.RectF
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

fun Rect.toScreenRectF(gameRect: Rect, screenWidth: Float, screenHeight: Float): RectF {
    fun mapX(x: Double) = ((x - gameRect.left) / gameRect.width * screenWidth).toFloat()
    fun mapY(y: Double) = ((gameRect.top - y) / (gameRect.top - gameRect.bottom) * screenHeight).toFloat()

    return RectF(mapX(left), mapY(top), mapX(right), mapY(bottom))
}

fun drawBird(
    canvas: Canvas,
    birdShape: Shape,
    appearance: BirdAppearance,
    gameRect: Rect,
    screenWidth: Float,
    screenHeight: Float
) {
    val bounds = birdShape.getBoundingRectangle().toScreenRectF(gameRect, screenWidth, screenHeight)
    appearance.draw(canvas, bounds)
}
