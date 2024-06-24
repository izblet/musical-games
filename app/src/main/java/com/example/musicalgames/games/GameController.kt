package com.example.musicalgames.games

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

interface GameListener {
    fun onGameEnded()
}

interface GameController {
    fun registerListener(listener: GameListener)
    fun unregisterListener(listener: GameListener)
    fun setViewModel(viewModel: ViewModel)
    fun initGame(context: Context, listener: GameListener)
    fun startGame(owner: LifecycleOwner)
    fun pauseGame()
    fun endGame()
    fun getScore(): Int
}