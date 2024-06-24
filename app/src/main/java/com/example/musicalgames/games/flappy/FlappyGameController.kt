package com.example.musicalgames.games.flappy

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.games.GameController
import com.example.musicalgames.games.GameListener
import com.example.musicalgames.games.flappy.game_view.FloppyGameView
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser
import com.example.musicalgames.games.flappy.ViewModel as FlappyViewModel
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
    private var viewModel: FlappyViewModel? = null

    override fun startGame(owner: LifecycleOwner) {
        isGameRunning = true
        startGameLoop(owner)
    }

    override fun pauseGame() {
        endGame()
    }

    override fun endGame() {
        isGameRunning = false
        viewModel!!.pitchRecogniser!!.release()
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

    override fun setViewModel(viewModel: ViewModel) {
        if(viewModel is FlappyViewModel) {
            this.viewModel = viewModel
        }
    }

    override fun initGame(context: Context) {
        val minListenedPitch = "C2"
        val maxListenedPitch = "C6"

        val pitchRecogniser = PitchRecogniser(context,
            minListenedPitch, maxListenedPitch)

        this.viewModel!!.pitchRecogniser = pitchRecogniser
        pitchRecogniser.start()
        gameView.setViewModelData(viewModel!!)
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
