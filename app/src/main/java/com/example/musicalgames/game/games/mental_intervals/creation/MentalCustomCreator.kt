package com.example.musicalgames.game.games.mental_intervals.creation

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.musicalgames.components.ui_components.EnumSpinner
import com.example.musicalgames.databinding.MentalCustomCreatorBinding
import com.example.musicalgames.databinding.ViewEarCustomCreatorBinding
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.mental_intervals.Type
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.Note
import com.example.musicalgames.utils.Octave
import com.example.musicalgames.utils.Scale

class MentalCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {
    private var _binding: MentalCustomCreatorBinding? = null
    private val binding get() = _binding!!

    private var modeSpinnerValue: EnumSpinner.SpinnerEnumValue<Type>

    init {
        _binding = MentalCustomCreatorBinding.inflate(LayoutInflater.from(context), this, true)

        modeSpinnerValue = binding.modeSpinner.setEnum(Type.INTERVAL_NOTE)

    }


    private fun getFieldVal(id: Int): String {
        return findViewById<EditText>(id).text.toString()
    }

    private fun getSpinnerVal(id: Int): String {
        return findViewById<Spinner>(id).selectedItem.toString()
    }

    private fun getNotesFromSelection(): List<ChromaticNote> {
        val selectedChromatic = binding.startingNotesSelector.getSelected()
        val noteList: MutableList<ChromaticNote> = mutableListOf()
        for (note in ChromaticNote.entries) {
            if (note in selectedChromatic) {
                noteList.add(note)
            }
        }
        return noteList
    }
    private fun getIntervalsFromSelection() : List<Interval> {
        val selectedIntervals = binding.intervalSelector.getSelected()
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

    override fun highlightMissing() {
        Toast.makeText(context, "Some fields are missing", Toast.LENGTH_SHORT).show()
    }
}
