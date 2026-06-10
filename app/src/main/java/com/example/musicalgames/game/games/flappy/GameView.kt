package com.example.musicalgames.games.flappy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.example.musicalgames.R
import com.example.musicalgames.components.StaffPainter
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.KeySignature
import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.wrappers.BitmapUtil
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

    private var viewModel: FlappyViewModel? = null
    private var treblePainter: StaffPainter
    private var bassPainter: StaffPainter

   init {
       val foregroundColor = TypedValue().let { tv ->
           context.theme.resolveAttribute(android.R.attr.colorForeground, tv, true)
           tv.data
       }

       val trebleClefBitmap = BitmapUtil.decodeSampledBitmap(resources, R.drawable.treble_clef, 256)
       val sharpBitmap = BitmapUtil.decodeSampledBitmap(resources, R.drawable.sharp, 256)
       val flatBitmap = BitmapUtil.decodeSampledBitmap(resources, R.drawable.flat, 256)
       treblePainter = StaffPainter(trebleClefBitmap, sharpBitmap, flatBitmap, foregroundColor).apply { setTreble(true) }

       val bassClefBitmap = BitmapUtil.decodeSampledBitmap(resources, R.drawable.bass_clef, 256)
       bassPainter = StaffPainter(bassClefBitmap, sharpBitmap, flatBitmap, foregroundColor).apply { setTreble(false) }
   }

    private val scorePaint = Paint().apply {
        color = context.getColor(R.color.white)
        textSize = 48f
    }

    private val backgroundColor: Int = TypedValue().let { tv ->
        context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
        tv.data
    }
    private val pipeColor = context.getColor(R.color.blue)

    fun setEndListener(listener: GameListener) {
        endListener = listener
    }
    fun setViewModelData (viewModel: FlappyViewModel) {
        this.viewModel=viewModel
        this.minNote = viewModel.minRange
        this.maxNote = viewModel.maxRange

        val keySignature = KeySignature.forKey(Note(viewModel.root).noteChromatic, viewModel.mode)
        treblePainter.setKeySignature(keySignature)
        bassPainter.setKeySignature(keySignature)

        val notespace = 1
        this.minVisible = MusicUtil.spiceNoteBottomEnd(minNote!!-notespace)
        this.maxVisible = MusicUtil.spiceNoteTopEnd(maxNote!!+notespace)

        //the +1 at the end is because we are adding half a note above and below in bottom/top end
        val displayedNotesNum = (maxNote!!-minNote!!+1)+2*notespace+1
        pitchSize = 1/displayedNotesNum.toFloat()

        this.bird = Bird(viewModel.pitchRecogniser!!, minVisible!!, maxVisible!!, pitchSize!!)
    }

    private fun getRandomPipe(): Pipe {
        val note = getRandom(viewModel!!.gapPositions)
        val staffPainter =
            if(note < Note(ChromaticNote.C, 4).midiCode) bassPainter
            else treblePainter

        createdPipes++
        return Pipe(
            pipeColor,
            1f,
            note,
            minVisible!!,
            maxVisible!!,
            pitchSize!!,
            staffPainter
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

        for (pipe in pipes) { pipe.drawLane(canvas, height.toFloat(), width.toFloat()) }
        bird!!.draw(canvas, height.toFloat(), width.toFloat())
        for (pipe in pipes) { pipe.draw(canvas, height.toFloat(), width.toFloat()) }

        // Draw the score
        canvas.drawText("Score: $score", 20f, 60f, scorePaint)
    }
    fun updateBird() {
        bird!!.updateTarget()
    }

    fun freezeBird() {
        bird?.freeze()
    }

    fun tickFrame() {
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

