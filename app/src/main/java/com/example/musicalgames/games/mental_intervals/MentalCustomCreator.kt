package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator

class MentalCustomCreator(context: Context, attrSet: AttributeSet?) : CustomGameCreator(context,attrSet) {
    override fun getLevel(): Level? {
        return null
    }

    override fun saveLevel() {
    }

}