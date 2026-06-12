package com.example.musicalgames.game.games.flappy.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicalgames.R
import com.example.musicalgames.game.games.flappy.graphics.FlappyRenderState
import com.example.musicalgames.game.games.flappy.graphics.flappy_renderer.FlappyRenderer
import com.example.musicalgames.game.games.flappy.FlappyViewModel
import com.example.musicalgames.utils.geometry.Rect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewGameView(
    context: Context,
    private val viewModel: FlappyViewModel,
    private val renderer: FlappyRenderer,
    private val gameRect: Rect,
    lifecycleOwner: LifecycleOwner
) : View(context) {

    private var state: FlappyRenderState? = null

    private val scorePaint = Paint().apply {
        color = context.getColor(R.color.white)
        textSize = 48f
    }


    init {

        //copied from previous version of project
        //this is supposed to trigger redraws, but without changing target direction for bird
        lifecycleOwner.lifecycleScope.launch {
                viewModel.renderState.collect {renderState ->
                    state=renderState
                    invalidate()
                }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val state = state
        if (state==null) return

        renderer.draw(canvas, state.birdShape, state.pipes, state.pipeNotes, gameRect, width.toFloat(), height.toFloat())
        canvas.drawText("Score: ${state.score}", 20f, 60f, scorePaint)
    }
}
