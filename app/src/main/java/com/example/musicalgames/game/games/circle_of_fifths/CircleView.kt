package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.settings.EnumColorSettings
import com.example.musicalgames.settings.EnumColorSettingsRepository
import com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette
import com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette.CirclePaletteListener
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteListener
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteView
import kotlinx.coroutines.launch

class CircleView(context: Context, private val viewModel: CircleViewModel, lifecycleOwner: LifecycleOwner) : LinearLayout(context), CirclePaletteListener {
    private val circle = CircleOfFifthsPalette(context, null)
    private val keyboard = KeyPaletteView(context)
    private val textView = TextView(context)
    private val coloursDisabled = EnumColorSettingsRepository(context).isEnumColorBlocked(Mode::class.java)
    private val modeColours = EnumColorSettingsRepository(context).get(Mode::class.java, EnumColorSettings::defaultColorFor)

    init {
        orientation = VERTICAL

        textView.textSize = 40f
        textView.gravity = Gravity.CENTER

        keyboard.visibility = GONE

        addView(circle, LayoutParams(LayoutParams.MATCH_PARENT, 0, 3f))
        addView(textView, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
        addView(keyboard, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        circle.registerListener(this)
        lifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect { value -> updateView(value) }
        }
    }

    private fun updateView(state: CircleViewState) {

        if(!coloursDisabled)
            state.currentMode?.let { circle.setHighlightColour(modeColours.getValue(it)) }

        circle.setHighlightedIndices(
            if (state.highlightedNote == null) listOf() else listOf(state.highlightedNote)
        )
        circle.setCenterText(state.question)
        //circle.setTextColour()
        keyboard.visibility = if (state.showKeyboard) VISIBLE else GONE
        textView.text = state.screenCommandMessage ?: ""
    }

    override fun onKeyClicked(index: Int) {
        viewModel.clickCircle(index)
    }

    fun setKeyboardListener(listener: KeyPaletteListener) {
        keyboard.registerListener(listener)
    }

}