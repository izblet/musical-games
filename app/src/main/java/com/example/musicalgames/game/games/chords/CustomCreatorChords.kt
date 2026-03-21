package com.example.musicalgames.game.games.chords

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.components.ui_components.EnumSpinner
import com.example.musicalgames.components.ui_components.KeyboardSelector
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.utils.Chord

class CustomCreatorChords(
    context: Context,
    createLevelAction: (Level) -> Unit,
    attrSet: AttributeSet?
) : CustomGameCreator(context, createLevelAction, attrSet) {
    //TODO: Spinner is of course not the right choice here
    private val qualitiesSpinner = EnumSpinner(context)
    private val qualitiesValue = qualitiesSpinner.setEnum<Chord.Companion.Quality>()
    private val extensionsSpinner = EnumSpinner(context)
    private val extensionsValue = extensionsSpinner.setEnum<Chord.Companion.Extension>()
    private val startingNotes = KeyboardSelector(context)

    init {
        addNewRow("Starting notes", startingNotes)
        addNewRow("Quality", qualitiesSpinner)
        addNewRow("Extension", extensionsSpinner)
    }
    override fun getLevel(): Level? {
        val quality = qualitiesValue.getSelectedValue()
        val extension = extensionsValue.getSelectedValue()
        val notes = startingNotes.getSelected()
        return LevelChords(notes, setOf(quality), setOf(extension))
    }

    override fun highlightMissing() {
    }

    override fun clearSelection() {
        startingNotes.clearSelection()
        extensionsSpinner.setSelection(0)
        qualitiesSpinner.setSelection(0)
    }
}