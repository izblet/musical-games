package com.example.musicalgames.games.flappy

import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.games.GameController
import com.example.musicalgames.games.GameListener
import com.example.musicalgames.games.flappy.game_view.FloppyGameView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlappyGameController(private val gameView: FloppyGameView) : GameController {
    private var isGameRunning = false
    private val handler = Handler()
    private val frameRateMillis = 1000 / 60 // 60 frames per second
    private var birdUpdateJob: Job? = null

    override fun startGame(owner: LifecycleOwner) {
        isGameRunning = true
        startGameLoop(owner)
    }

    override fun pauseGame() {
        endGame()
    }

    override fun endGame() {
        isGameRunning = false
        birdUpdateJob?.cancel()
    }

    override fun getScore(): Int {
        return gameView.getScore()
    }

    override fun registerListener(listener: GameListener) {
       gameView.setEndListener(listener)
    }

    override fun unregisterListener(listener: GameListener) {
       //TODO: implement this
    }

    private fun startGameLoop(owner: LifecycleOwner) {
        birdUpdateJob = owner.lifecycleScope.launch {
            while (isGameRunning) {
                withContext(Dispatchers.IO) {
                    gameView.updateBird()
                }
                delay(frameRateMillis.toLong())
            }
        }

        handler.post(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    gameView.updateView()
                    gameView.invalidate()
                    handler.postDelayed(this, frameRateMillis.toLong())
                }
            }
        })
    }

}
