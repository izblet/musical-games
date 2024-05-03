package com.example.musicalgames.controllers

import android.os.Handler
import android.os.Looper
import com.example.musicalgames.views.GameView

class GameController(private val gameView: GameView, private val birdController: BirdController) {
    private var isGameRunning = false
    private val handler = Handler()
    private val frameRateMillis = 1000 / 60  // Update at 60 frames per second
    var backgroundHandler = Handler(Looper.getMainLooper())

    fun startGame() {
        // Initialize game state, reset score, etc.
        isGameRunning = true
        gameView.addPipes()
        startGameLoop()
        // Add any additional initialization logic as needed
    }

    fun stopGame() {
        // Stop the game loop and clean up resources
        isGameRunning = false
        // Add any additional cleanup logic as needed
    }

    private fun startGameLoop() {
        handler.post(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    backgroundHandler.post{
                        //TODO:accessing a variable that is supposed to be private is suboptimal
                        birdController.updatePosition(gameView.bird, gameView.height.toFloat())
                    }

                    gameView.update()  // Update game state
                    gameView.invalidate()  // Redraw the view
                    handler.postDelayed(this, frameRateMillis.toLong())
                }
            }
        })
    }

    fun handleCollision() {
        // Handle collision between bird and pipes
    }
}
