package com.example.musicalgames.game.games.flappy.graphics.flappy_renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.musicalgames.game.games.flappy.graphics.BirdAppearance
import com.example.musicalgames.game.games.flappy.graphics.drawBird
import com.example.musicalgames.game.games.flappy.graphics.drawKeyboardTiles
import com.example.musicalgames.game.games.flappy.graphics.drawLane
import com.example.musicalgames.game.games.flappy.graphics.toScreenRectF
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.utils.components.StaffPainter
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

class StaffFlappyRenderer(
    private val treblePainter: StaffPainter,
    private val bassPainter: StaffPainter,
    private val birdAppearance: BirdAppearance,
    laneColor: Int,
    private val keyPadding: Double
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
        pipes.forEach { (rect, _, _) ->
            drawLane(
                canvas,
                rect,
                gameRect,
                screenWidth,
                screenHeight,
                lanePaint
            )
        }

        drawBird(canvas, birdShape, birdAppearance, gameRect, screenWidth, screenHeight)

        val staffTop = (screenHeight - STAFF_HEIGHT) / 2
        treblePainter.setConstraints(staffTop, staffTop + STAFF_HEIGHT)
        bassPainter.setConstraints(staffTop, staffTop + STAFF_HEIGHT)

        val middleC = Note(ChromaticNote.C, 4).midiCode

        pipes.forEachIndexed { index, (rect, _, _) ->
            drawKeyboardTiles(
                canvas,
                rect,
                gameRect,
                screenWidth,
                screenHeight,
                whitePaint,
                blackPaint,
                keyPadding
            )

            val staffRight = rect.toScreenRectF(gameRect, screenWidth, screenHeight).left - STAFF_MARGIN
            val note = pipeNotes[index]
            val staffPainter = if (note < middleC) bassPainter else treblePainter
            staffPainter.drawStaff(canvas, staffRight, listOf(note))
        }
    }
}