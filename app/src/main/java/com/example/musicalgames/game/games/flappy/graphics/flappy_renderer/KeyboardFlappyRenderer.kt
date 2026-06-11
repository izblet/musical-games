package com.example.musicalgames.game.games.flappy.graphics.flappy_renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.musicalgames.game.games.flappy.graphics.BirdAppearance
import com.example.musicalgames.game.games.flappy.graphics.drawBird
import com.example.musicalgames.game.games.flappy.graphics.drawKeyboardTiles
import com.example.musicalgames.game.games.flappy.graphics.drawLane
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

class KeyboardFlappyRenderer(
    private val birdAppearance: BirdAppearance,
    laneColor: Int,
    private val keyPadding: Double
) : FlappyRenderer {

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

        pipes.forEach { (_, rectTop, rectBottom) ->
            rectTop?.let {
                drawKeyboardTiles(
                    canvas,
                    it,
                    gameRect,
                    screenWidth,
                    screenHeight,
                    whitePaint,
                    blackPaint,
                    .0
                )
            }
            rectBottom?.let {
                drawKeyboardTiles(
                    canvas,
                    it,
                    gameRect,
                    screenWidth,
                    screenHeight,
                    whitePaint,
                    blackPaint,
                    keyPadding
                )
            }
        }

        drawBird(canvas, birdShape, birdAppearance, gameRect, screenWidth, screenHeight)
    }
}