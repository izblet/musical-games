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

    private var minVisible: Double? = null
    private var maxVisible: Double? = null

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
        this.minNote = MusicUtil.midi(viewModel.minRange)
        this.maxNote = MusicUtil.midi(viewModel.maxRange)

        val notespace = 1
        this.minVisible = MusicUtil.spiceNoteBottomEnd(minNote!!-notespace)
        this.maxVisible = MusicUtil.spiceNoteTopEnd(maxNote!!+notespace)

        //the +1 at the end is because we are adding half a note above and below in bottom/top end
        val displayedNotesNum = (maxNote!!-minNote!!+1)+2*notespace+1
        val pitchSize = 1/displayedNotesNum.toFloat()

        this.bird = Bird(pitchRecogniser!!, minVisible!!, maxVisible!!, pitchSize.toFloat())
    }

    private fun getRandomPipe(): Pipe {
        return Pipe(
            pipeColor,
            1f,
            generateRandomGap(minNote!!,maxNote!!),
            minVisible!!,
            maxVisible!!
        )
    }

    private fun generateRandomGap(min: Int, max: Int): Int {
        //returns a note that will correspond to the gap
        return Random.nextInt(min, max+1)
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
    fun updateBird() {
        bird!!.updateTarget()

    }

    fun updateView() {
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

class Bird(private val pitchRecogniser: PitchRecogniser, private val minPitch: Double, private val maxPitch: Double, pitchSize: Float) {
    //should be passed in the constructor
    private var x: Float = 0.5f
    private var y: Float = 0.1f
    private val radius: Float= (pitchSize/4)
    private val downwardSpeed = 0.025f
    private val moveSpeedDiv = 20
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
        // the radius is vertical radius, when displayed on the screen it has to be scaled
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
        //otherwise it is a number between 0 and 1 corresponding to spice value of the note
        val pitch = pitchRecogniser.getPitch()

        if(pitch == -1f)
            targetY.set(y+downwardSpeed)
        else {
            val normalizedPitch = MusicUtil.normalize(pitch.toDouble(), minPitch, maxPitch)
            targetY.set(1-normalizedPitch.toFloat())
        }
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