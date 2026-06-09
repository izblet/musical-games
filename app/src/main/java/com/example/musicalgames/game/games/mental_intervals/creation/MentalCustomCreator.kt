package com.example.musicalgames.game.games.mental_intervals.creation

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.example.musicalgames.R
import com.example.musicalgames.components.ui_components.EnumSpinner
import com.example.musicalgames.components.ui_components.IntervalSelector
import com.example.musicalgames.components.ui_components.KeyboardSelector
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
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

    private fun makeLevelOrThrow(): Level {
        val intervals = getIntervalsFromSelection()
        val startingNotes = getNotesFromSelection()
        val mode = modeSpinnerValue.getSelectedValue()

        return MentalLevel(startingNotes, intervals, mode)
    }

    override fun getLevel(): Level? {
        try {
            val level = makeLevelOrThrow()
            Log.d("level", level.toString())
            return level
        } catch (e: Exception) {
            Log.d("level", e.toString())
            return null
        }
    }
    override fun clearSelection() {
        modeSpinner.setSelection(0)
        notesSelector.clearSelection()
        intervalSelector.clearSelection()
    }

    override fun highlightMissing() {
        Toast.makeText(context, "Some fields are missing", Toast.LENGTH_SHORT).show()
    }
}
