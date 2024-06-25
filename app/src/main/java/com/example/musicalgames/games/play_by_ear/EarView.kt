package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.example.musicalgames.components.KeyboardListener
import com.example.musicalgames.components.PianoKeyboardView
import com.example.musicalgames.utils.Note
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager

class EarView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs), KeyboardListener {

    private var pianoKeyboardView: PianoKeyboardView
    private val soundPlayer : DefaultSoundPlayerManager by lazy { DefaultSoundPlayerManager(context) }

    private var score: Int = 0
    private var problem : List<Note> = listOf()
    private var index : Int = 0
    private var viewModel: EarViewModel? = null

    init {
        pianoKeyboardView = PianoKeyboardView(context, null)
        pianoKeyboardView.setRange(Note("C4"), Note("G4"))

        addView(pianoKeyboardView)
        pianoKeyboardView.registerListener(this)
    }
    fun setViewModel(viewModel: EarViewModel) {
        this.viewModel=viewModel
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

    override fun onKeyClicked(key: Note) {
        soundPlayer.play(key.midiCode)
    }
}
