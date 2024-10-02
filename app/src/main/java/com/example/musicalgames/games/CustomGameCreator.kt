package com.example.musicalgames.games

import android.content.Context
import android.util.AttributeSet
import android.view.View

abstract class CustomGameCreator(context :Context, attrSet: AttributeSet?) : View(context, attrSet) {
    abstract fun startGame()
    abstract fun saveGame()
}