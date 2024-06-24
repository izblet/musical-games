package com.example.musicalgames.games

import androidx.lifecycle.LifecycleOwner

interface GameListener {
    fun onGameEnded()
}

interface GameController {
    fun registerListener(listener: GameListener)
    fun unregisterListener(listener: GameListener)
    fun startGame(owner: LifecycleOwner)
    fun pauseGame()
    fun endGame()
    fun getScore(): Int
}