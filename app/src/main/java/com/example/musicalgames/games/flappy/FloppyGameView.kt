package com.example.musicalgames.games.flappy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.musicalgames.R
import kotlinx.coroutines.Job
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random
interface GameEndListener {
    fun onEndGame()
}

class FloppyGameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val pipes = mutableListOf<Pipe>()
    private var bird: Bird? = null
    private var endListener: GameEndListener? = null
    private var score = 0
    private var updateBird: Job? = null

    private val scorePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 48f
    }

    private val backgroundColor = ContextCompat.getColor(context, R.color.black)
    private val pipeColor = ContextCompat.getColor(context, R.color.blue)

    fun setEndListener(listener: GameEndListener) {
        endListener = listener
    }
    fun setBird(bird: Bird) {
        this.bird = bird
    }

    companion object {
        const val PIPE_GAP = 200f
        const val PIPE_DISTANCE = 400
    }

    fun getRandomPipe(lastPipeX: Float): Pipe {
        return Pipe(
            pipeColor,
            lastPipeX + PIPE_DISTANCE,
            generateRandomHeight(),
            PIPE_GAP
        )
    }

    fun addPipes() {
        viewTreeObserver.addOnGlobalLayoutListener {
            if(pipes.size == 0 )
                pipes.add(getRandomPipe(screenWidth.toFloat()))
        }
    }

    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(backgroundColor)
        bird!!.draw(canvas)

        for (pipe in pipes) {
            pipe.draw(canvas, height.toFloat())
        }

        // Draw the score
        canvas.drawText("Score: $score", 20f, 60f, scorePaint)
    }
    fun updateBird(viewHeight: Float) {
        bird!!.updateTarget(viewHeight)

    }

    fun updateView(viewHeight: Float) {
        bird!!.updatePosition(viewHeight)
        for (pipe in pipes) {
            pipe.move()

            if (bird!!.intersects(pipe))
                endListener?.onEndGame()
        }

        // Check if the bird has passed a pipe
        if (pipes.isNotEmpty() && bird!!.passing(pipes[0]))
            score++

        pipes.removeAll { it.x + Pipe.WIDTH < 0 }
        if (pipes.size != 0 && pipes.last().isVisible(width))
            pipes.add(getRandomPipe(pipes.last().x))

        invalidate()
    }

    fun getScore() : Int {
        return score
    }
    private fun generateRandomHeight(): Float {
        return Random.nextInt(200, (height - PIPE_GAP - 200).toInt()).toFloat()
    }

    private val screenWidth: Int
        get() = resources.displayMetrics.widthPixels
}

class Pipe(
    val color: Int,
    var x: Float,
    var topHeight: Float,
    gap: Float
) {
    companion object {
        const val WIDTH = 100
        const val SPEED = 5
    }

    private val paint = Paint()
    val bottomY = topHeight + gap
    init {
        paint.color=color
    }

    fun move() {
        x -= SPEED
    }
    fun draw(canvas: Canvas, screenHeight: Float) {
        canvas.drawRect(x, 0f, x + WIDTH, topHeight, paint)
        canvas.drawRect(x, bottomY, x + WIDTH, screenHeight, paint)
    }

    fun isVisible(screenWidth: Int): Boolean {
        return x + WIDTH > 0 && x < screenWidth
    }
}

class Bird(private val pitchRecogniser: PitchRecogniser) {
    //should be passed in the constructor
    private var x: Float = 100f
    private var y: Float = 100f
    private val radius: Float=20f
    private var targetY = AtomicReference(0f)
    private val paint = Paint()
    init {
        paint.color = Color.RED
    }
    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }
    fun passing(pipe: Pipe) : Boolean {
        return x > pipe.x + Pipe.WIDTH && x < pipe.x + Pipe.WIDTH + Pipe.SPEED
    }
    fun intersects(pipe: Pipe):Boolean {
        val birdRect = RectF(
            x - radius,
            y - radius,
            x + radius,
            y + radius
        )
        val topPipeRect = RectF(
            pipe.x,
            0f,
            pipe.x + Pipe.WIDTH,
            pipe.topHeight
        )
        val bottomPipeRect = RectF(
            pipe.x,
            pipe.bottomY,
            pipe.x + Pipe.WIDTH,
            Float.MAX_VALUE
        )
        return birdRect.intersect(topPipeRect) || birdRect.intersect(bottomPipeRect)
    }
    fun updateTarget(maxCoordinate: Float) {
        //pitch is -1 if does not exist because of an error or low confidence level
        //otherwise it is a number between 0 and 1, but with different semantics than that of tensorflow
        //0 is the bottom of the screen, 1 is the top of the screen

        val pitch = pitchRecogniser.getPitch()
        targetY.set(
            if(pitch == -1f)
                y + 15
            else
                (1-pitch)*maxCoordinate //1-pitch is here because we are getting coordinates from top
        )
    }

    fun updatePosition(maxCoordinate: Float) {
        //the position of the bird display will be calculated (maxCoordinate*value)
        targetY.let {
            val deltaY = (it.get() - y) / 10
            if(y+deltaY>0 && y+deltaY<maxCoordinate)
                y += deltaY
        }
    }
}