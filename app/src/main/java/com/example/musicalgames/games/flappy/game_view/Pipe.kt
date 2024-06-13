package com.example.musicalgames.games.flappy.game_view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.musicalgames.utils.MusicUtil

class Pipe(
    color: Int,
    var x: Float,
    private val gap: Int,
    private val minVisible: Double,
    private val maxVisible: Double
) {
    //x is the coordinate of left side of the pipe, gap is the coordinate of the middle of the gap
    companion object {
        const val WIDTH = 0.05f
        const val SPEED = 0.002f
        const val PIPE_SPACE = 0.3f
    }

    private val paint = Paint()
    init {
        paint.color=color
    }

    fun move() {
        x -= SPEED
    }
    fun getTopRect(): RectF {
        val nextNote = gap+1;
        val topEnd = 1f - MusicUtil.normalize(nextNote, minVisible, maxVisible)
        val leftEnd = x;
        val rightEnd = x + WIDTH
        return RectF(leftEnd, 0f, rightEnd, topEnd.toFloat())
    }
    fun getBottomRect(): RectF {
        val prevNote = gap-1;
        val bottomEnd = 1f - MusicUtil.normalize(prevNote, minVisible, maxVisible)
        val leftEnd = x;
        val rightEnd = x + WIDTH
        return RectF(leftEnd, bottomEnd.toFloat(), rightEnd, 1f)
    }
    private fun scale(rect: RectF, scaleX:Float, scaleY:Float) {
        rect.left *=scaleX
        rect.top *=scaleY
        rect.right*=scaleX
        rect.bottom*=scaleY
    }

    fun draw(canvas: Canvas, screenHeight: Float, screenWidth: Float) {
        val topRect = getTopRect()
        scale(topRect, screenWidth, screenHeight)
        val bottomRect = getBottomRect()
        scale(bottomRect, screenWidth, screenHeight)

        canvas.drawRect(topRect, paint)
        canvas.drawRect(bottomRect, paint)
    }

    fun passedLastPosition() : Boolean {
        return x < (1f- PIPE_SPACE)
    }
}