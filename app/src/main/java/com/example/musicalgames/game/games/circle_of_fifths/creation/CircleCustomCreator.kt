package com.example.musicalgames.game.games.circle_of_fifths.creation

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.example.musicalgames.R
import com.example.musicalgames.components.ui_components.EnumSpinner
import com.example.musicalgames.components.ui_components.MultiEnumSpinner
import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.music_model.display.ModeSpelling

private enum class QuestionDirection {
    CIRCLE_TO_NOTE,
    NOTE_TO_CIRCLE;
    override fun toString(): String {
        return if (this == CIRCLE_TO_NOTE) {
            "Circle to note"
        } else {
            "Note to circle"
        }
    }
}

class CircleCustomCreator(context: Context, createLevelAction: (Level) -> Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {

    //TODO: the themeWrapper is temporary and the spinner should set its own style from a stub
    private val inputThemedContext = ContextThemeWrapper(context, R.style.customInputElementStyle)
    private val modeSpinner = EnumSpinner(inputThemedContext)
    private val modeSpinnerValue = modeSpinner.setEnum(QuestionDirection.CIRCLE_TO_NOTE)
    private val modesSpinner = MultiEnumSpinner(context)
    private val modesValue = modesSpinner.setEnum<Mode>(displayName = ModeSpelling::common)

    init {
        addNewRow("Mode", modeSpinner)
        addNewRow("Modes:", modesSpinner)
    }

    constructor(context: Context, level: CircleLevel, attrs: AttributeSet?) : this(context, {}, attrs) {
        modeSpinnerValue.setSelectedValue(if (level.positionToName) QuestionDirection.CIRCLE_TO_NOTE else QuestionDirection.NOTE_TO_CIRCLE)
        modesValue.setSelectedValues(level.modes.toSet())
    }

    override fun getLevel(): Level? {
        val modes = modesValue.getSelectedValues().toList()
        if (modes.isEmpty()) return null
        val positionToName = modeSpinnerValue.getSelectedValue() == QuestionDirection.CIRCLE_TO_NOTE
        return CircleLevel(positionToName = positionToName, modes = modes)
    }

    override fun highlightMissing() {
    }

    override fun clearSelection() {
        modeSpinner.setSelection(0)
        modesValue.resetToDefault()
    }
}
