package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.example.musicalgames.utils.components.palettes.interval_palette.IntervalPaletteListener
import com.example.musicalgames.utils.components.palettes.interval_palette.IntervalPaletteView
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteListener
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteView
import com.example.musicalgames.game.games.mental_intervals.MentalViewmodelListener
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Interval
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference
import com.example.musicalgames.settings.EnumColorSettingsRepository
import com.example.musicalgames.settings.EnumColorSettings
import kotlin.math.roundToInt

class MentalView(context: Context) : ViewGroup(context), MentalViewmodelListener {

    private var viewModel: MentalViewModel? = null
    private val notePaletteRatio :Float = 2f/3f
    private val keyPaletteView: KeyPaletteView = KeyPaletteView(context)
    private val intervalPaletteView: IntervalPaletteView = IntervalPaletteView(context)
    private val messageTextView: TextView = TextView(context).apply {
        textSize = 20f
        gravity = Gravity.CENTER_HORIZONTAL
        text = ""
    }
    // user-configurable via the Settings screen - loaded once, since there's no path from
    // gameplay back to Settings without leaving/ending this game first

    private val colorsDisabled = EnumColorSettingsRepository(context).isEnumColorBlocked(Interval::class.java)
    private val intervalColors: Map<Interval, Int> =
        if(!colorsDisabled) {
            EnumColorSettingsRepository(context).get(
        Interval::class.java, EnumColorSettings::defaultColorFor)
        } else Interval.entries.associateWith { Color.WHITE }

    init {
        addView(messageTextView)
        addView(keyPaletteView)
        addView(intervalPaletteView)
    }
    fun setViewModel(viewModel: MentalViewModel) {
        this.viewModel =viewModel
        redraw()
    }
    fun setKeyboardListener(listener: KeyPaletteListener) {
        keyPaletteView.registerListener(listener)
    }
    fun setIntervalListener(listener: IntervalPaletteListener) {
        intervalPaletteView.registerListener(listener)
    }

    private fun redraw() {
        if(viewModel!!.type == Type.INTERVAL_NOTE)
            intervalPaletteView.visibility = View.GONE
        else
            keyPaletteView.visibility = View.GONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val messageHeight = (height * 1) / 10
        messageTextView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageHeight, MeasureSpec.EXACTLY)
        )

        val paletteHeight :Int = (notePaletteRatio * height.toFloat()).roundToInt()

        keyPaletteView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        )

        intervalPaletteView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(width, height)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val keyPaletteHeight = (height.toFloat() * notePaletteRatio).roundToInt()
        val messageHeight = (height * 1) / 10

        messageTextView.layout(0, 0, width, messageHeight)
        keyPaletteView.layout(0, height - keyPaletteHeight, width, height)
        intervalPaletteView.layout(0, height - keyPaletteHeight, width, height)
    }

    override fun onNewProblem(interval: Interval, questionNote: ChromaticNote) {

        messageTextView.text = buildSpannedString {
            append("${NoteSpelling.spell(questionNote, SpellingPreference.MIXED)} + ")
            color(intervalColors.getValue(interval)) {
                append("$interval")
            }

        }
    }

    override fun onNewProblem(questionNote: ChromaticNote, note: ChromaticNote) {
       messageTextView.text = "${NoteSpelling.spell(questionNote, SpellingPreference.MIXED)} + ? = ${NoteSpelling.spell(note, SpellingPreference.MIXED)}"
    }

    override fun onRightAnswer() {
        messageTextView.text = "Good!"
    }

    override fun onWrongAnswer(correctAns: Interval) {
        messageTextView.text = "The right answer is : ${correctAns.name}"
    }

    override fun onWrongAnswer(correctAns: ChromaticNote) {
        messageTextView.text = "The right answer is : ${NoteSpelling.spell(correctAns, SpellingPreference.MIXED)}"
    }
}
