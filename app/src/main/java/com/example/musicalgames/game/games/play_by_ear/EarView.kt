package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.utils.components.keyboard.KeyboardListener
import com.example.musicalgames.utils.components.keyboard.KeyboardView
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.utils.wrappers.sound_playing.DefaultSoundPlayerManager
import kotlinx.coroutines.launch


import android.widget.TextView
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel

class EarView(
    context: Context,
    attrs: AttributeSet?,
    private val viewModel: EarViewModel,
    lifecycleOwner: LifecycleOwner
) : ViewGroup(context, attrs), KeyboardListener {

    private var keyboardView: KeyboardView = KeyboardView(context, null)
    private val soundPlayer : DefaultSoundPlayerManager by lazy { DefaultSoundPlayerManager(context) }
    private var keyboardDisabled = true
    private val level: PlayEarLevel = viewModel.level!!
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
                viewModel.playRoot()
            }
        }
        //TODO: think about what to do with this button, disabled for now
        nextButton = Button(context).apply {
            text = "Next Problem"
            setOnClickListener{
                if(viewModel.problemFinished()) {
                    viewModel.newProblem()
                }
            }
        }

        addView(messageTextView)
        addView(keyboardView)
        addView(rootButton)
        addView(nextButton)

        keyboardView.registerListener(this)

        viewModel.setPlayer(soundPlayer)
        keyboardView.setRange(Note(level.minPitchDisplayed), Note(level.maxPitchDisplayed))
        level.getDisplayedRoot()?.let { keyboardView.setColoured(it) }

        lifecycleOwner.lifecycleScope.launch {
            viewModel.renderState.collect { render(it) }
        }
    }

    private fun render(state: EarRenderState) {
        messageTextView.text = state.message
        keyboardDisabled = !state.keyboardEnabled
    }

    override fun onKeyClicked(key: Note) {
        if (keyboardDisabled)
            return
        viewModel.selectNote(key)
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
