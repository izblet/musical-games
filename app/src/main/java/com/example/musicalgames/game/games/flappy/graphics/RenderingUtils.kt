package com.example.musicalgames.game.games.flappy.graphics

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape
import kotlin.math.roundToInt

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

//draws a pipe's full-height background lane, slightly wider than the pipe itself
fun drawLane(
    canvas: Canvas,
    pipeRect: Rect,
    gameRect: Rect,
    screenWidth: Float,
    screenHeight: Float,
    paint: Paint
) {
    val padding = pipeRect.width * 1.5
    val laneRect = Rect(
        left = pipeRect.left - padding,
        top = pipeRect.top,
        right = pipeRect.right + padding,
        bottom = pipeRect.bottom
    )
    canvas.drawRect(laneRect.toScreenRectF(gameRect, screenWidth, screenHeight), paint)
}

//draws one tile per key covered by `rect`, colored like a piano key (key for note m spans [m-0.5, m+0.5])
fun drawKeyboardTiles(
    canvas: Canvas,
    rect: Rect,
    gameRect: Rect,
    screenWidth: Float,
    screenHeight: Float,
    whitePaint: Paint,
    blackPaint: Paint
) {
    var note = rect.bottom.roundToInt()
    while (note - 0.5 < rect.top) {
        val tile = Rect(
            left = rect.left,
            top = (note + 0.5).coerceAtMost(rect.top),
            right = rect.right,
            bottom = (note - 0.5).coerceAtLeast(rect.bottom)
        )
        val paint = if (MusicUtil.isWhite(note)) whitePaint else blackPaint
        canvas.drawRect(tile.toScreenRectF(gameRect, screenWidth, screenHeight), paint)

        note += 1
    }
}
