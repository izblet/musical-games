package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator

class EarCustomCreator(context : Context, attrSet: AttributeSet?) : CustomGameCreator(context, attrSet) {
    override fun getLevel(): Level? {
        return null
    }

    override fun saveLevel() {
    }
}