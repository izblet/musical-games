package com.example.musicalgames.games.flappy

import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.games.flappy.game_view.FloppyGameView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameController(private val gameView: FloppyGameView) {
    private var isGameRunning = false
    private val handler = Handler()
    private val frameRateMillis = 1000 / 60 // 60 frames per second
    private var birdUpdateJob: Job? = null

    fun startGame(owner: LifecycleOwner) {
        isGameRunning = true
        gameView.addPipes()
        startGameLoop(owner)
    }

    fun pauseGame() {
        isGameRunning = false
    }

    fun endGame() {
        isGameRunning = false
        birdUpdateJob?.cancel()
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
