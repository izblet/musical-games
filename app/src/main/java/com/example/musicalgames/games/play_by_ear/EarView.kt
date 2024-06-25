package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import com.example.musicalgames.components.KeyboardListener
import com.example.musicalgames.components.PianoKeyboardView
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.utils.Note
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.wrappers.sound_playing.SoundPlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class EarView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs), KeyboardListener, SoundPlayerListener {

    private var pianoKeyboardView: PianoKeyboardView
    private val soundPlayer : DefaultSoundPlayerManager by lazy { DefaultSoundPlayerManager(context) }

    private var endListener: GameListener? = null
    private var keyboardDisabled = true

    private var score: Int = 0
    private var problem : List<Note> = listOf()
    private var index : Int = 0
    private var viewModel: EarViewModel? = null

    init {
        pianoKeyboardView = PianoKeyboardView(context, null)

        addView(pianoKeyboardView)
        pianoKeyboardView.registerListener(this)
    }
    fun setViewModel(viewModel: EarViewModel) {
        this.viewModel=viewModel
        pianoKeyboardView.setRange(viewModel.minKey!!, viewModel.maxKey!!)
    }
    private fun playProblem() {
        keyboardDisabled = true
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            soundPlayer.playSequence(problem, this@EarView)
        }
    }

    private fun getRandomNote() : Note {
        return viewModel!!.available.random()
    }
    private fun generateProblem() {
        val notes = mutableListOf(getRandomNote())
        while(notes.size<viewModel!!.notesNum) {
            val newNote = getRandomNote()
            if(abs(notes[notes.size - 1].midiCode - newNote.midiCode) <=viewModel!!.maxInterval)
                notes.add(newNote)
        }
        problem = notes
    }
    fun newProblem() {
        index = 0
        generateProblem()
        playProblem()
    }
    fun getScore() : Int {
        return score
    }

    override fun onKeyClicked(key: Note) {
        if(keyboardDisabled)
            return
        soundPlayer.play(key.midiCode)
        if(index >= problem.size)
            return

        if(problem[index]!=key) {
            endListener?.onGameEnded()
            return
        }
        index++
        if(index==problem.size) {
            score++
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                newProblem()
            },1000)
        }
    }
    fun playRoot() {
        soundPlayer.play(viewModel!!.root)
    }
    fun registerEndListener(listener: GameListener) {
        this.endListener = listener
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val pianoHeight = (height * 1) / 2
        pianoKeyboardView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(pianoHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val pianoHeight = (height * 1) / 2

        pianoKeyboardView.layout(0, height - pianoHeight, width, height)
    }

    override fun onSoundFinished() {
        keyboardDisabled=false
    }

}
