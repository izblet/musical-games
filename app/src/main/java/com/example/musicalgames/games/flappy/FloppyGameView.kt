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
import com.example.musicalgames.games.MusicUtil
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random
interface GameEndListener {
    fun onEndGame()
}

class FloppyGameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var endListener: GameEndListener? = null
    private var pitchRecogniser: PitchRecogniser? = null
    private val pipes = mutableListOf<Pipe>()
    private var bird: Bird? = null
    private var score = 0
    private var minNote: Int? = null
    private var maxNote: Int? = null

    private var viewModel: ViewModel? = null

    private val scorePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 48f
    }

    private val backgroundColor = ContextCompat.getColor(context, R.color.black)
    private val pipeColor = ContextCompat.getColor(context, R.color.blue)

    fun setEndListener(listener: GameEndListener) {
        endListener = listener
    }
    fun setViewModel (viewModel: ViewModel) {
        this.viewModel=viewModel
        this.pitchRecogniser = viewModel.pitchRecogniser
        this.bird = Bird(pitchRecogniser!!)
        this.minNote = MusicUtil.midi(viewModel.minRange)
        this.maxNote = MusicUtil.midi(viewModel.maxRange)
    }

    private fun getRandomPipe(): Pipe {
        return Pipe(
            pipeColor,
            1f,
            generateRandomGap(minNote!!,maxNote!!),
            minNote!!,
            maxNote!!
        )
    }

    private fun generateRandomGap(min: Int, max: Int): Int {
        //returns a note that will correspond to the gap
        return Random.nextInt(min+1, max)
    }

    fun addPipes() {
        viewTreeObserver.addOnGlobalLayoutListener {
            if(pipes.size == 0 )
                pipes.add(getRandomPipe())
        }
    }

    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(backgroundColor)

        bird!!.draw(canvas, height.toFloat(), width.toFloat())

        for (pipe in pipes) {
            pipe.draw(canvas, height.toFloat(), width.toFloat())
        }

        // Draw the score
        canvas.drawText("Score: $score", 20f, 60f, scorePaint)
    }
    fun updateBird(viewHeight: Float) {
        bird!!.updateTarget()

    }

    fun updateView(viewHeight: Float) {
        bird!!.updatePosition()
        for (pipe in pipes) {
            pipe.move()

            if (bird!!.intersects(pipe, height.toFloat(), width.toFloat()))
                endListener?.onEndGame()

            else if(bird!!.passing(pipe))
                score++
        }

        pipes.removeAll { it.x + Pipe.WIDTH < 0 }

        if (pipes.size != 0 && pipes.last().passedLastPosition())
            pipes.add(getRandomPipe())

        invalidate()
    }

    fun getScore() : Int {
        return score
    }
}

class Pipe(
    color: Int,
    var x: Float,
    private val gap: Int,
    minNote: Int,
    maxNote: Int
) {
    //x is the coordinate of left side of the pipe, gap is the coordinate of the middle of the gap
    companion object {
        const val WIDTH = 0.05f
        const val SPEED = 0.002f
        const val PIPE_SPACE = 0.3f
    }

    private val paint = Paint()
    private val minVisible = MusicUtil.spiceNoteBottomEnd(minNote)
    private val maxVisible = MusicUtil.spiceNoteTopEnd(maxNote)
    init {
        paint.color=color
    }

    fun move() {
        x -= SPEED
    }
    fun getTopRect():RectF {
        val nextNote = gap+1;
        val topEnd = 1f - MusicUtil.normalize(nextNote, minVisible, maxVisible)
        val leftEnd = x;
        val rightEnd = x + WIDTH
        return RectF(leftEnd, 0f, rightEnd, topEnd.toFloat())
    }
    fun getBottomRect():RectF {
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

class Bird(private val pitchRecogniser: PitchRecogniser) {
    //should be passed in the constructor
    private var x: Float = 0.5f
    private var y: Float = 0.1f
    private val radius: Float=0.03f
    private val downwardSpeed = 0.03f
    private val moveSpeedDiv = 10
    private var targetY = AtomicReference(0f)
    private val paint = Paint()
    init {
        paint.color = Color.RED
    }
    fun draw(canvas: Canvas, screenHeight: Float, screenWidth: Float) {
        canvas.drawCircle(x*screenWidth, y*screenHeight, radius*screenHeight, paint)
    }
    fun passing(pipe: Pipe) : Boolean {
        return x > pipe.x + Pipe.WIDTH && x < pipe.x + Pipe.WIDTH + Pipe.SPEED
    }
    fun intersects(pipe: Pipe, width:Float, height:Float):Boolean {
        // the radius is vertical radius, when displayed on the screen it is scaled
        val horizontalRadius = radius*(width/height)
        val birdRect = RectF(
            x - horizontalRadius,
            y - radius,
            x + horizontalRadius,
            y + radius
        )
        val topPipeRect = pipe.getTopRect()
        val bottomPipeRect = pipe.getBottomRect()

        return birdRect.intersect(topPipeRect) || birdRect.intersect(bottomPipeRect)
    }
    fun updateTarget() {
        //pitch is -1 if does not exist because of an error or low confidence level
        //otherwise it is a number between 0 and 1, but with different semantics than that of tensorflow
        //0 is the bottom of the screen, 1 is the top of the screen

        val pitch = pitchRecogniser.getPitch()
        targetY.set(
            if(pitch == -1f)
                y + downwardSpeed
            else
                (1-pitch) //1-pitch is here because we are getting coordinates from top
        )
    }

    fun updatePosition() {
        //the position of the bird display will be calculated (maxCoordinate*value)
        targetY.let {
            val deltaY = (it.get() - y) / moveSpeedDiv
            if(y+deltaY>0 && y+deltaY<1)
                y += deltaY
        }
    }
}