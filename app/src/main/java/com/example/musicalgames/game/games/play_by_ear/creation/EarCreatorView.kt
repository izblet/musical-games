package com.example.musicalgames.game.games.play_by_ear.creation
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.musicalgames.components.ui_components.EnumSpinner
import com.example.musicalgames.databinding.ViewEarCustomCreatorBinding
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Interval
import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.Octave
import com.example.musicalgames.music_model.Scale
import com.example.musicalgames.music_model.display.SpellingPreference


class EarCreatorView(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {
    private var _binding: ViewEarCustomCreatorBinding? = null
    private val binding get() = _binding!!
    private lateinit var scaleSpinnerValue: EnumSpinner.SpinnerEnumValue<Scale>
    private lateinit var rootSpinnerValue: EnumSpinner.SpinnerEnumValue<ChromaticNote>
    private lateinit var maxIntervalSpinner: EnumSpinner.SpinnerEnumValue<Interval>
    private lateinit var minSoundNoteSpinner: EnumSpinner.SpinnerEnumValue<ChromaticNote>
    private lateinit var maxSoundNoteSpinner: EnumSpinner.SpinnerEnumValue<ChromaticNote>
    private lateinit var minSoundOctaveSpinner: EnumSpinner.SpinnerEnumValue<Octave>
    private lateinit var maxSoundOctaveSpinner: EnumSpinner.SpinnerEnumValue<Octave>
    private fun setValues() {
        rootSpinnerValue=binding.rootSpinner.setEnum()
        scaleSpinnerValue = binding.scaleSpinner.setEnum()
    }

    private fun setDefaultValues() {
        val noteSpelling: ((ChromaticNote)->String)= { note -> NoteSpelling.spell(note, SpellingPreference.MIXED) }
        rootSpinnerValue = binding.rootSpinner.setEnum(ChromaticNote.C,noteSpelling)
        scaleSpinnerValue = binding.scaleSpinner.setEnum(Scale.MAJOR)
        maxIntervalSpinner = binding.maxIntervalSpinner.setEnum(Interval.P8)
        minSoundNoteSpinner = binding.minNoteSpinner.setEnum(ChromaticNote.C, noteSpelling)
        maxSoundNoteSpinner = binding.maxNoteSpinner.setEnum(ChromaticNote.C,noteSpelling)
        minSoundOctaveSpinner = binding.minNoteOctave.setEnum(Octave.o4)
        maxSoundOctaveSpinner = binding.maxOctaveSpinner.setEnum(Octave.o5)

    }

    init {
        _binding = ViewEarCustomCreatorBinding.inflate(LayoutInflater.from(context), this, true)

       setDefaultValues()

        val setSelectionMethod: ()->Unit = {
            if(binding.isSelectionToggle.isChecked) {
                binding.customNotesRow.visibility = View.VISIBLE
                binding.scaleRow.visibility = View.GONE
            } else {
                binding.customNotesRow.visibility = View.GONE
                binding.scaleRow.visibility = View.VISIBLE
            }
        }
        setSelectionMethod()

        binding.isSelectionToggle.setOnClickListener{
            setSelectionMethod()
        }


    }


    private fun getFieldVal(id: Int): String {
        return findViewById<EditText>(id).text.toString()
    }

    private fun getSpinnerVal(id: Int): String {
        return findViewById<Spinner>(id).selectedItem.toString()
    }

    private fun getNotesFromSelection (min: Int, max: Int): List<Int> {
        val selectedChromatic = binding.keyboardSelector.getSelected()
        val noteList : MutableList<Int> = mutableListOf()
        for(note in min until max+1) {
            if(Note(note).noteChromatic in selectedChromatic) {
                noteList.add(note)
            }
        }
        return noteList
    }
    private fun getNotesFromScale(min: Int, max: Int) : List<Int> {
        val scale = scaleSpinnerValue.getSelectedValue()
        val root = rootSpinnerValue.getSelectedValue()
        Log.d("level", scale.toString())
        Log.d("level", root.name)
        return MusicUtil.getScaleNotesBetween(scale, root, Note(min), Note(max)).map { it.midiCode }
    }

    private fun makeLevelOrThrow(): Level {
        val len = binding.editLen.text!!.toString().toInt()
        val root = rootSpinnerValue.getSelectedValue()
        val maxInterval = maxIntervalSpinner.getSelectedValue().getSemitones()
        val minNote = Note(minSoundNoteSpinner.getSelectedValue(), minSoundOctaveSpinner.getSelectedValue()).midiCode
        val maxNote = Note(maxSoundNoteSpinner.getSelectedValue(), maxSoundOctaveSpinner.getSelectedValue()).midiCode
        if(maxNote<=minNote) throw IllegalArgumentException("maxNote is greater than minNote")
        val noteList = if(binding.isSelectionToggle.isChecked) {
                getNotesFromSelection(minNote, maxNote)
            } else {
                getNotesFromScale(minNote, maxNote)
            }

        return PlayEarLevel(minNote, maxNote, root, len, maxInterval, noteList)
    }

    override fun getLevel(): Level? {
        try {
            val level = makeLevelOrThrow()
            Log.d("level", level.toString())
            return level
        } catch (e: Exception) {
            Log.d("level",e.toString())
            return null
        }
    }

    override fun highlightMissing() {
        Toast.makeText(context, "Some fields are missing", Toast.LENGTH_SHORT).show()
    }

    override fun clearSelection() {
        rootSpinnerValue.resetToDefault()
        scaleSpinnerValue.resetToDefault()
        maxIntervalSpinner.resetToDefault()
        minSoundNoteSpinner.resetToDefault()
        maxSoundNoteSpinner.resetToDefault()
        minSoundOctaveSpinner.resetToDefault()
        maxSoundOctaveSpinner.resetToDefault()
        binding.editLen.setText("")
        binding.keyboardSelector.clearSelection()
    }

}