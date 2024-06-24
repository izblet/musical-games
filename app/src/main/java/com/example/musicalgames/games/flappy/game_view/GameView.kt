package com.example.musicalgames.games.flappy.game_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.musicalgames.R
import com.example.musicalgames.games.GameListener
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.games.flappy.ViewModel
import com.example.musicalgames.games.flappy.level_list.LEN_INF
import kotlin.random.Random

class FloppyGameView(context: Context) : View(context) {

    private var endListener: GameListener? = null
    private val pipes = mutableListOf<Pipe>()
    private var bird: Bird? = null
    private var score = 0
    private var minNote: Int? = null
    private var maxNote: Int? = null
    private var pitchSize: Float? = null
    private var createdPipes = 0

    private var minVisible: Double? = null
    private var maxVisible: Double? = null

    private var viewModel: ViewModel? = null

    private val scorePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 48f
    }

    private val backgroundColor = ContextCompat.getColor(context, R.color.blue)
    private val pipeColor = ContextCompat.getColor(context, R.color.blue)

    fun setEndListener(listener: GameListener) {
        endListener = listener
    }
    fun setViewModelData (viewModel: ViewModel) {
        this.viewModel=viewModel
        this.minNote = viewModel.minRange
        this.maxNote = viewModel.maxRange

        val notespace = 1
        this.minVisible = MusicUtil.spiceNoteBottomEnd(minNote!!-notespace)
        this.maxVisible = MusicUtil.spiceNoteTopEnd(maxNote!!+notespace)

        //the +1 at the end is because we are adding half a note above and below in bottom/top end
        val displayedNotesNum = (maxNote!!-minNote!!+1)+2*notespace+1
        pitchSize = 1/displayedNotesNum.toFloat()

        this.bird = Bird(viewModel.pitchRecogniser!!, minVisible!!, maxVisible!!, pitchSize!!)
    }

    private fun getRandomPipe(): Pipe {
        createdPipes++
        return Pipe(
            pipeColor,
            1f,
            getRandom(viewModel!!.gapPositions!!),
            minVisible!!,
            maxVisible!!,
            pitchSize!!
        )
    }
    private fun getRandom(notes: List<Int>): Int {
        return notes[Random.nextInt(0, notes.size)]
    }

    private fun generateRandomGap(min: Int, max: Int): Int {
        //returns a note that will correspond to the gap
        return Random.nextInt(min, max+1)
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
                endListener?.onGameEnded()

            else if(bird!!.passing(pipe)) {
                score++
                if(viewModel!!.endAfter == score)
                    endListener?.onGameEnded()
            }
        }

        pipes.removeAll { it.x + Pipe.WIDTH < 0 }

        if(viewModel!!.endAfter == LEN_INF || createdPipes < viewModel!!.endAfter) {
            if (pipes.size == 0 || pipes.last().passedLastPosition())
                pipes.add(getRandomPipe())
        }

        invalidate()
    }

    fun getScore() : Int {
        return score
    }
}

