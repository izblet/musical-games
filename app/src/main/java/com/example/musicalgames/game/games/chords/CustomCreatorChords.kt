package com.example.musicalgames.game.games.chords

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator

class CustomCreatorChords(
    context: Context,
    createLevelAction: (Level) -> Unit,
    attrSet: AttributeSet?
) : CustomGameCreator(context, createLevelAction, attrSet) {
    override fun getLevel(): Level? {
        TODO("Not yet implemented")
    }

    override fun highlightMissing() {
        TODO("Not yet implemented")
    }

    override fun clearSelection() {
        TODO("Not yet implemented")
    }
}