package com.example.musicalgames.games.flappy

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator

class FlappyCustomCreator(context : Context, attrSet: AttributeSet?) : CustomGameCreator(context, attrSet) {
    override fun getLevel(): Level? {
        return null
    }

    override fun saveLevel() {

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }
}