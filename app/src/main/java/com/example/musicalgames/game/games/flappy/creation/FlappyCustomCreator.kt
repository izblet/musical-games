package com.example.musicalgames.game.games.flappy.creation

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.ToggleButton
import android.widget.Toast
import com.example.musicalgames.R
import com.example.musicalgames.utils.components.ui_components.EnumSpinner
import com.example.musicalgames.utils.components.ui_components.KeyboardSelector
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.games.flappy.LEN_INF
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.Octave
import com.example.musicalgames.music_model.Scale
import com.example.musicalgames.music_model.display.ModeSpelling
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference

class FlappyCustomCreator(context: Context, createLevelAction: (Level) -> Unit, attrs: AttributeSet?) :
    CustomGameCreator(context, createLevelAction, attrs) {

    //TODO: the themeWrapper is temporary and the spinner should set its own style from a stub
    private val inputThemedContext = ContextThemeWrapper(context, R.style.customInputElementStyle)
    private val noteSpelling: (ChromaticNote) -> String = { note -> NoteSpelling.spell(note, SpellingPreference.MIXED) }

    private val rootSpinner = EnumSpinner(inputThemedContext)
    private val rootSpinnerValue = rootSpinner.setEnum(ChromaticNote.C, noteSpelling)

    private val modeSpinner = EnumSpinner(inputThemedContext)
    private val modeSpinnerValue = modeSpinner.setEnum(Mode.IONIAN, ModeSpelling::common)

    private val isSelectionToggle = ToggleButton(context, null, 0, R.style.customInputElementStyle).apply {
        textOff = "Scale"
        textOn = "Selection"
        gravity = Gravity.CENTER
        setSingleLine(true)
    }

    private val scaleSpinner = EnumSpinner(inputThemedContext)
    private val scaleSpinnerValue = scaleSpinner.setEnum(Scale.MAJOR)

    private val notesSelector = KeyboardSelector(context)

    private val minNoteSpinner = EnumSpinner(inputThemedContext)
    private val minNoteSpinnerValue = minNoteSpinner.setEnum(ChromaticNote.C, noteSpelling)
    private val minOctaveSpinner = EnumSpinner(inputThemedContext)
    private val minOctaveSpinnerValue = minOctaveSpinner.setEnum(Octave.o4)

    private val maxNoteSpinner = EnumSpinner(inputThemedContext)
    private val maxNoteSpinnerValue = maxNoteSpinner.setEnum(ChromaticNote.C, noteSpelling)
    private val maxOctaveSpinner = EnumSpinner(inputThemedContext)
    private val maxOctaveSpinnerValue = maxOctaveSpinner.setEnum(Octave.o5)

    private val scaleRow: TableRow
    private val selectionRow: TableRow

    init {
        addNewRow("Key signature:", rootSpinner, modeSpinner)
        addNewRow("Notes from:", isSelectionToggle)
        scaleRow = addNewRow("Scale:", scaleSpinner)
        selectionRow = addNewRow("Selection:", notesSelector)
        addNewRow("Min Sound:", minNoteSpinner, minOctaveSpinner)
        addNewRow("Max Sound:", maxNoteSpinner, maxOctaveSpinner)

        isSelectionToggle.setOnClickListener { updateNotesFromVisibility() }
        updateNotesFromVisibility()
    }

    constructor(context: Context, level: FlappyLevel, attrs: AttributeSet?) : this(context, {}, attrs) {
        rootSpinnerValue.setSelectedValue(Note(level.root).noteChromatic)
        modeSpinnerValue.setSelectedValue(level.mode)

        val minNote = Note(level.minPitch)
        minNoteSpinnerValue.setSelectedValue(minNote.noteChromatic)
        minOctaveSpinnerValue.setSelectedValue(Octave.entries[minNote.octave - 1])

        val maxNote = Note(level.maxPitch)
        maxNoteSpinnerValue.setSelectedValue(maxNote.noteChromatic)
        maxOctaveSpinnerValue.setSelectedValue(Octave.entries[maxNote.octave - 1])

        notesSelector.setSelected(level.keyList.map { Note(it).noteChromatic }.toSet())

        isSelectionToggle.isChecked = true
        updateNotesFromVisibility()
    }

    private fun updateNotesFromVisibility() {
        if (isSelectionToggle.isChecked) {
            scaleRow.visibility = View.GONE
            selectionRow.visibility = View.VISIBLE
        } else {
            scaleRow.visibility = View.VISIBLE
            selectionRow.visibility = View.GONE
        }
    }

    private fun getNotesFromSelection(min: Int, max: Int): List<Int> {
        val selectedChromatic = notesSelector.getSelected()
        val noteList = mutableListOf<Int>()
        for (note in min..max) {
            if (Note(note).noteChromatic in selectedChromatic) {
                noteList.add(note)
            }
        }
        return noteList
    }

    private fun getNotesFromScale(min: Int, max: Int): List<Int> {
        val scale = scaleSpinnerValue.getSelectedValue()
        val root = rootSpinnerValue.getSelectedValue()
        return MusicUtil.getScaleNotesBetween(scale, root, Note(min), Note(max)).map { it.midiCode }
    }

    private fun getRootPitch(root: ChromaticNote, min: Int, max: Int): Int {
        for (note in min..max) {
            if (Note(note).noteChromatic == root) return note
        }
        //root isn't in range - fall back to the highest occurrence of it below min
        var pitch = Note(root, Note(min).octave).midiCode
        if (pitch >= min) pitch -= 12
        return pitch
    }

    private fun makeLevelOrThrow(): Level {
        val root = rootSpinnerValue.getSelectedValue()
        val mode = modeSpinnerValue.getSelectedValue()
        val minNote = Note(minNoteSpinnerValue.getSelectedValue(), minOctaveSpinnerValue.getSelectedValue()).midiCode
        val maxNote = Note(maxNoteSpinnerValue.getSelectedValue(), maxOctaveSpinnerValue.getSelectedValue()).midiCode
        if (maxNote <= minNote) throw IllegalArgumentException("maxNote must be greater than minNote")

        val keyList = if (isSelectionToggle.isChecked) {
            getNotesFromSelection(minNote, maxNote)
        } else {
            getNotesFromScale(minNote, maxNote)
        }
        if (keyList.isEmpty()) throw IllegalArgumentException("no notes selected")

        val rootPitch = getRootPitch(root, minNote, maxNote)

        return FlappyLevel(minNote, maxNote, rootPitch, mode, keyList, LEN_INF)
    }

    override fun getLevel(): Level? {
        return try {
            val level = makeLevelOrThrow()
            Log.d("level", level.toString())
            level
        } catch (e: Exception) {
            Log.d("level", e.toString())
            null
        }
    }

    override fun highlightMissing() {
        Toast.makeText(context, "Some fields are missing", Toast.LENGTH_SHORT).show()
    }

    override fun clearSelection() {
        rootSpinnerValue.resetToDefault()
        modeSpinnerValue.resetToDefault()
        scaleSpinnerValue.resetToDefault()
        minNoteSpinnerValue.resetToDefault()
        minOctaveSpinnerValue.resetToDefault()
        maxNoteSpinnerValue.resetToDefault()
        maxOctaveSpinnerValue.resetToDefault()
        notesSelector.clearSelection()
        isSelectionToggle.isChecked = false
        updateNotesFromVisibility()
    }
}
