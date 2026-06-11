package com.example.musicalgames.game.games.flappy.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.musicalgames.utils.components.StaffPainter
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

class StaffFlappyRenderer(
    private val staffPainter: StaffPainter,
    private val birdAppearance: BirdAppearance,
    laneColor: Int
) : FlappyRenderer {

    companion object {
        private const val STAFF_HEIGHT = 200f
        private const val STAFF_MARGIN = 20f
    }

    private val whitePaint = Paint().apply { color = Color.WHITE }
    private val blackPaint = Paint().apply { color = Color.BLACK }
    private val lanePaint = Paint().apply { color = laneColor }

    override fun draw(
        canvas: Canvas,
        birdShape: Shape,
        pipes: List<Triple<Rect, Rect?, Rect?>>,
        pipeNotes: List<Int>,
        gameRect: Rect,
        screenWidth: Float,
        screenHeight: Float
    ) {
        pipes.forEach { (rect, _, _) -> drawLane(canvas, rect, gameRect, screenWidth, screenHeight, lanePaint) }

        drawBird(canvas, birdShape, birdAppearance, gameRect, screenWidth, screenHeight)

        val staffTop = (screenHeight - STAFF_HEIGHT) / 2
        staffPainter.setConstraints(staffTop, staffTop + STAFF_HEIGHT)

        pipes.forEachIndexed { index, (rect, _, _) ->
            drawKeyboardTiles(canvas, rect, gameRect, screenWidth, screenHeight, whitePaint, blackPaint)

            val staffRight = rect.toScreenRectF(gameRect, screenWidth, screenHeight).left - STAFF_MARGIN
            staffPainter.drawStaff(canvas, staffRight, listOf(pipeNotes[index]))
        }
    }
}
