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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.musicalgames.game.games.play_by_ear.EarViewmodelListener
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.noteName

class EarView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs), KeyboardListener, EarViewmodelListener {

    private var keyboardView: KeyboardView = KeyboardView(context, null)
    private val soundPlayer : DefaultSoundPlayerManager by lazy { DefaultSoundPlayerManager(context) }
    private var endListener: GameListener? = null
    private var keyboardDisabled = true
    private var score: Int = 0
    private var viewModel: EarViewModel? = null
    private var level: PlayEarLevel = PlayEarLevel(-1,-1,-1,ChromaticNote.C, 0, -1,listOf(), "","")
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
                viewModel!!.playRoot()
            }
        }
        nextButton = Button(context).apply {
            text = "Next Problem"
            setOnClickListener{
                if(viewModel!!.isProblemSolved()) {
                    viewModel!!.newProblem()
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
        this.viewModel!!.setPlayer(soundPlayer)
        this.viewModel!!.registerListener(this)
        keyboardView.setRange(Note(level.minPitchDisplayed), Note(level.maxPitchDisplayed))
    }


    fun getScore(): Int {
        return score
    }

    override fun onKeyClicked(key: Note) {
        if (keyboardDisabled)
            return
        viewModel!!.selectNote(key)
    }

    fun registerEndListener(listener: GameListener) {
        this.endListener = listener
    }

    override fun onNewProblem() {
        messageTextView.text = "Play the melody"
    }
    override fun onPlaybackStarted() {
        messageTextView.text = "Listen to the melody..."
        keyboardDisabled = true
    }
    override fun onPlaybackFinished() {
        keyboardDisabled = false
        messageTextView.text = "Play the melody"
    }
    override fun onRightAnswer() {
        messageTextView.text="Good!"
    }
    override fun onWrongAnswer() {
        endListener?.onGameEnded()
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

}
