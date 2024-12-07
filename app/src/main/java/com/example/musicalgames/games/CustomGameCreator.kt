package com.example.musicalgames.games

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.musicalgames.game_activity.Level

abstract class CustomGameCreator(context :Context, attrSet: AttributeSet?) : ViewGroup(context, attrSet) {
    abstract fun getLevel(): Level?
    abstract fun saveLevel()
}