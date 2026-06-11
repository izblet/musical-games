package com.example.musicalgames.game.games.flappy.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.R
import com.example.musicalgames.game.games.flappy.graphics.FlappyRenderState
import com.example.musicalgames.game.games.flappy.graphics.flappy_renderer.FlappyRenderer
import com.example.musicalgames.game.games.flappy.FlappyViewModel
import com.example.musicalgames.utils.geometry.Rect
import kotlinx.coroutines.launch

class NewGameView(
    context: Context,
    private val viewModel: FlappyViewModel,
    private val renderer: FlappyRenderer,
    private val gameRect: Rect,
    lifecycleOwner: LifecycleOwner
) : View(context) {

    private var renderState: FlappyRenderState? = null

    private val scorePaint = Paint().apply {
        color = context.getColor(R.color.white)
        textSize = 48f
    }

    init {
        lifecycleOwner.lifecycleScope.launch {
            viewModel.renderState.collect { state ->
                renderState = state
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val state = renderState ?: return

        renderer.draw(canvas, state.birdShape, state.pipes, state.pipeNotes, gameRect, width.toFloat(), height.toFloat())
        canvas.drawText("Score: ${state.score}", 20f, 60f, scorePaint)
    }
}
