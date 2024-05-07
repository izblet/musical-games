package com.example.musicalgames.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.musicalgames.R
import com.example.musicalgames.models.Bird
import com.example.musicalgames.models.Pipe
import kotlin.random.Random
interface GameEndListener {
    fun onEndGame()
}


class FloppyGameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val pipes = mutableListOf<Pipe>()
    val bird: Bird = Bird()
    private val paint = Paint()
    private var targetY: Float? = null
    private var endListener: GameEndListener? = null
    private var score = 0

    private val scorePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 48f
    }

    private val backgroundColor = ContextCompat.getColor(context, R.color.black)
    private val pipeColor = ContextCompat.getColor(context, R.color.blue)

    fun setEndListener(listener: GameEndListener) {
        endListener = listener
    }

    companion object {
        const val PIPE_GAP = 200
        const val PIPE_DISTANCE = 400
    }

    fun getRandomPipe(lastPipeX: Int): Pipe {
        return Pipe(
            lastPipeX + PIPE_DISTANCE,
            generateRandomHeight(),
            PIPE_GAP
        )
    }

    fun addPipes() {
        viewTreeObserver.addOnGlobalLayoutListener {
            pipes.add(getRandomPipe(screenWidth))
        }
    }

    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background
        canvas.drawColor(backgroundColor)

        paint.color = Color.RED
        canvas.drawCircle(bird.x, bird.y, bird.radius, paint)

        paint.color = pipeColor

        for (pipe in pipes) {
            canvas.drawRect(pipe.x.toFloat(), 0f, (pipe.x + Pipe.WIDTH).toFloat(), pipe.topHeight.toFloat(), paint)
            canvas.drawRect(pipe.x.toFloat(), pipe.bottomY.toFloat(), (pipe.x + Pipe.WIDTH).toFloat(), height.toFloat(), paint)
        }

        // Draw the score
        canvas.drawText("Score: $score", 20f, 60f, scorePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                targetY = event.y
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun update() {
        for (pipe in pipes) {
            pipe.move()
            val birdRect = RectF(
                bird.x - bird.radius,
                bird.y - bird.radius,
                bird.x + bird.radius,
                bird.y + bird.radius
            )
            val topPipeRect = RectF(
                pipe.x.toFloat(),
                0f,
                pipe.x.toFloat() + Pipe.WIDTH,
                pipe.topHeight.toFloat()
            )
            val bottomPipeRect = RectF(
                pipe.x.toFloat(),
                pipe.bottomY.toFloat(),
                pipe.x.toFloat() + Pipe.WIDTH,
                height.toFloat()  // Use the height of the GameView as the bottom boundary
            )
            if (birdRect.intersect(topPipeRect) || birdRect.intersect(bottomPipeRect)) {
                endListener?.onEndGame()
            }
        }

        // Check if the bird has passed a pipe
        if (pipes.isNotEmpty() && bird.x > pipes[0].x + Pipe.WIDTH && bird.x < pipes[0].x + Pipe.WIDTH + 5) {
            score++
        }

        pipes.removeAll { it.x + Pipe.WIDTH < 0 }
        if (pipes.size != 0 && pipes.last().isVisible(width))
            pipes.add(getRandomPipe(pipes.last().x))
        invalidate()
    }

    private fun generateRandomHeight(): Int {
        return Random.nextInt(200, height - PIPE_GAP - 200)
    }

    private val screenWidth: Int
        get() = resources.displayMetrics.widthPixels
}
