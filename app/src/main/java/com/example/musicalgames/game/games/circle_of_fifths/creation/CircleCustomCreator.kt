package com.example.musicalgames.game.games.circle_of_fifths.creation

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator

class CircleCustomCreator(context:Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator(context, createLevelAction, attrs) {
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