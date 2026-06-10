package com.example.musicalgames.game.games.circle_of_fifths.creation

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.components.ui_components.MultiEnumSpinner
import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.music_model.display.ModeSpelling

class CircleCustomCreator(context: Context, createLevelAction: (Level) -> Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {

    private val modesSpinner = MultiEnumSpinner(context)
    private val modesValue = modesSpinner.setEnum<Mode>(displayName = ModeSpelling::common)

    init {
        addNewRow("Modes:", modesSpinner)
    }

    constructor(context: Context, level: CircleLevel, attrs: AttributeSet?) : this(context, {}, attrs) {
        modesValue.setSelectedValues(level.modes.toSet())
        setEditable(false)
    }

    override fun getLevel(): Level? {
        val modes = modesValue.getSelectedValues().toList()
        if (modes.isEmpty()) return null
        return CircleLevel(positionToName = true, modes = modes)
    }

    override fun highlightMissing() {
    }

    override fun clearSelection() {
        modesValue.resetToDefault()
    }
}