package com.example.musicalgames.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.Toast
import com.example.musicalgames.models.Pipe
import kotlin.random.Random


class GameView(context: Context) : View(context) {
    private val pipes = mutableListOf<Pipe>()
    private val paint = Paint()

    companion object {
        const val PIPE_GAP = 200
        const val PIPE_DISTANCE = 400
    }

    init {
        viewTreeObserver.addOnGlobalLayoutListener {
            for (i in 1..3) {
                pipes.add(
                    Pipe(
                        screenWidth + i * PIPE_DISTANCE,
                        generateRandomHeight(),
                        generateRandomHeight(),
                        PIPE_GAP
                    )
                )
            }
        }
    }
    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = Color.GREEN

        for (pipe in pipes) {
            canvas.drawRect(pipe.x.toFloat(), 0f, (pipe.x + Pipe.WIDTH).toFloat(), pipe.topHeight.toFloat(), paint)
            canvas.drawRect(pipe.x.toFloat(), pipe.bottomY.toFloat(), (pipe.x + Pipe.WIDTH).toFloat(), height.toFloat(), paint)
        }
    }

    fun update() {

        for (pipe in pipes) {
            pipe.move()
        }
        pipes.removeAll { it.x+Pipe.WIDTH<0 }
        invalidate()
    }

    private fun generateRandomHeight(): Int {
        return Random.nextInt(200, height - PIPE_GAP - 200)
    }

    private val screenWidth: Int
        get() = resources.displayMetrics.widthPixels
}
