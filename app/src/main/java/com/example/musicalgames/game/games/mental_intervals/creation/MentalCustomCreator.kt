package com.example.musicalgames.game.games.mental_intervals.creation

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.example.musicalgames.R
import com.example.musicalgames.utils.components.ui_components.EnumSpinner
import com.example.musicalgames.utils.components.ui_components.IntervalSelector
import com.example.musicalgames.utils.components.ui_components.KeyboardSelector
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.game.game_core.creation.LevelCreationResult
import com.example.musicalgames.games.mental_intervals.Type
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Interval

class MentalCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {

    //TODO: the themeWrapper is temporary and the spinner should set its own style from a stub
    private val inputThemedContext = ContextThemeWrapper(context, R.style.customInputElementStyle)
    private val modeSpinner = EnumSpinner(inputThemedContext)
    private val modeSpinnerValue = modeSpinner.setEnum(Type.INTERVAL_NOTE)
    private val notesSelector = KeyboardSelector(context)
    private val intervalSelector = IntervalSelector(context)

    init {
        addNewRow("Mode", modeSpinner)
        addNewRow("Starting Notes", notesSelector)
        addNewRow("Intervals", intervalSelector)
    }

    constructor(context: Context, level: MentalLevel, attrs: AttributeSet?) : this(context, {}, attrs) {
        modeSpinnerValue.setSelectedValue(level.mode)
        notesSelector.setSelected(level.startingNotes.toSet())
        intervalSelector.setSelected(level.intervals.toSet())
    }

    private fun getNotesFromSelection(): List<ChromaticNote> {
        //TODO: there is probably a simpler way
        val selectedChromatic = notesSelector.getSelected()
        val noteList: MutableList<ChromaticNote> = mutableListOf()
        for (note in ChromaticNote.entries) {
            if (note in selectedChromatic) {
                noteList.add(note)
            }
        }
        return noteList
    }
    private fun getIntervalsFromSelection() : List<Interval> {
        //TODO: there is probably a simpler way
        val selectedIntervals = intervalSelector.getSelected()
        val intervalList: MutableList<Interval> = mutableListOf()
        for (interval in Interval.entries) {
            if (interval in selectedIntervals) {
                intervalList.add(interval)
            }
        }
        return intervalList
    }

    override fun getLevel(): LevelCreationResult {
        val intervals = getIntervalsFromSelection()
        val startingNotes = getNotesFromSelection()
        val mode = modeSpinnerValue.getSelectedValue()
        if(startingNotes.isEmpty()) return LevelCreationResult.Failure("no starting notes chosen")
        if(intervals.isEmpty()) return LevelCreationResult.Failure("no intervals chosen")

        return LevelCreationResult.Success(MentalLevel(startingNotes, intervals, mode))
    }
    override fun clearSelection() {
        modeSpinner.setSelection(0)
        notesSelector.clearSelection()
        intervalSelector.clearSelection()
    }

    override fun highlightMissing() {
    }
}
