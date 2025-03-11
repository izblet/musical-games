package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import com.example.musicalgames.components.keyboard.KeyboardListener
import com.example.musicalgames.components.keyboard.KeyboardView
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.utils.Note
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.wrappers.sound_playing.SoundPlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs


import android.widget.TextView
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.noteName

class EarView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs), KeyboardListener, SoundPlayerListener {

    private var keyboardView: KeyboardView = KeyboardView(context, null)
    private val soundPlayer : DefaultSoundPlayerManager by lazy { DefaultSoundPlayerManager(context) }
    private var endListener: GameListener? = null
    private var keyboardDisabled = true
    private var score: Int = 0
    private var problem : List<Note> = listOf()
    private var index : Int = 0
    private var viewModel: EarViewModel? = null
    private var level: PlayEarLevel = PlayEarLevel(-1,-1,-1,ChromaticNote.C, 0, -1,listOf(), "","")
    private var available: List<Note> = listOf()
    private val messageTextView: TextView
    private val rootButton: Button
    private val nextButton: Button

    init {
        messageTextView = TextView(context).apply {
            textSize = 20f
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
        }
        rootButton = Button(context).apply {
            text = "Play Root"
            setOnClickListener {
                playRoot()
            }
        }
        nextButton = Button(context).apply {
            text = "Next Problem"
            setOnClickListener{
                if(index==problem.size) {
                    newProblem()
                }
            }
        }


        addView(messageTextView)
        addView(keyboardView)
        addView(rootButton)
        addView(nextButton)

        keyboardView.registerListener(this)
    }

    fun setViewModel(viewModel: EarViewModel) {
        this.viewModel = viewModel
        level = viewModel.level!!
        keyboardView.setRange(Note(level.minPitchDisplayed), Note(level.maxPitchDisplayed))
        available = level.keyList.map { Note(it) }
        //keyboardView.setColoured(Note(viewModel.root))
    }



    private fun playProblem() {
        keyboardDisabled = true
        messageTextView.text = "Listen to the melody..."
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            soundPlayer.playSequence(problem, this@EarView)
        }
    }

    private fun getRandomNote(): Note {
        return available.random()
    }

    private fun generateProblem() {
        val notes = mutableListOf(getRandomNote())
        while (notes.size < level.problemLen) {
            val newNote = getRandomNote()
            if (abs(notes[notes.size - 1].midiCode - newNote.midiCode) <= level.maxSemitoneInterval)
                notes.add(newNote)
        }
        problem = notes
    }

    fun newProblem() {
        messageTextView.text = "Play the melody"
        index = 0
        generateProblem()
        playProblem()
    }

    fun getScore(): Int {
        return score
    }

    override fun onKeyClicked(key: Note) {
        if (keyboardDisabled)
            return
        soundPlayer.play(key.midiCode)
        if (index >= problem.size)
            return

        if (problem[index] != key) {
            endListener?.onGameEnded()
            return
        }
        index++
        if (index == problem.size) {
            score++
            messageTextView.text="Good!"
        }
    }

    fun playRoot() {
        messageTextView.text = "Root note: ${level.root}"
        //TODO: the following assumes that we have at least one note available, this should be checked somewhere
        soundPlayer.play(MusicUtil.midi(level.root.name+available.get(0).octave))
    }
    fun getCorrectNote() : String {
        if(index<problem.size)
            return problem[index].name
        return ""
    }

    fun registerEndListener(listener: GameListener) {
        this.endListener = listener
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val pianoHeight = (height * 1) / 2
        val messageHeight = (height * 1) / 10
        val buttonHeight = (height * 1) / 10

        messageTextView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageHeight, MeasureSpec.EXACTLY)
        )
        rootButton.measure(
            MeasureSpec.makeMeasureSpec(width / 4, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(buttonHeight, MeasureSpec.EXACTLY)
        )
        nextButton.measure(
            MeasureSpec.makeMeasureSpec(width / 4, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(buttonHeight, MeasureSpec.EXACTLY)
        )
        keyboardView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(pianoHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val messageHeight = (height * 1) / 10
        val buttonHeight = (height * 1) / 10
        val pianoHeight = (height * 1) / 2

        rootButton.layout(0, 0, width / 4, buttonHeight)
        nextButton.layout(3*width/4, 0, width, buttonHeight)
        messageTextView.layout(0, buttonHeight, width, buttonHeight + messageHeight)
        keyboardView.layout(0, height - pianoHeight, width, height)
    }

    override fun onSoundFinished() {
        messageTextView.text = "Play the melody"
        keyboardDisabled = false
    }
}
