package com.example.musicalgames.game.games.mental_intervals.creation

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator

class MentalCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrSet: AttributeSet?) : CustomGameCreator(context, createLevelAction,attrSet) {
    override fun getLevel(): Level? {
        return null
    }

    override fun saveLevel() {
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

}