package com.example.musicalgames.games

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.musicalgames.game_activity.Level

abstract class CustomGameCreator(context :Context, private val createLevelAction: (Level)->Unit, attrSet: AttributeSet?) : ViewGroup(context, attrSet) {
    abstract fun getLevel(): Level?
    abstract fun highlightMissing()

    fun saveLevel() : Boolean {
        val level = getLevel()
        if (level == null) {
            highlightMissing()
            return false
        } else {
            createLevelAction(level)
            return true
        }
    }
}