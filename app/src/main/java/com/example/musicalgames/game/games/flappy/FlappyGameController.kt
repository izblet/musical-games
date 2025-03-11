package com.example.musicalgames.games.flappy

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.wrappers.sound_playing.SoundPlayerManager
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser
import com.example.musicalgames.games.flappy.FlappyViewModel as FlappyViewModel
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
    private var soundPlayer: SoundPlayerManager? =null
    private var pitchRecogniser: PitchRecogniser? = null
    companion object {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    }
    override fun startGame(owner: LifecycleOwner) {
        soundPlayer!!.play(viewModel!!.minRange)
        pitchRecogniser!!.start()
        val handler = Handler(Looper.getMainLooper())
        //TODO: this is of course temporary - played sounds should be a part of the level class or sth
        //  maybe a field called "resolution" that the boundaries resolve to
        //  or just simply the root
        handler.postDelayed(
            {soundPlayer!!.play(viewModel!!.maxRange)},
            1000
        )
        handler.postDelayed(
            {soundPlayer!!.play(viewModel!!.root)},
            2000
        )
        handler.postDelayed({
            isGameRunning = true
            startGameLoop(owner)
        }, 3000)
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
        Log.e("score controller", "${gameView.getScore()}")
        return gameView.getScore()
    }

    override fun getEndDescription(): String {
        return ""
    }


    override fun setViewModel(viewModel: ViewModel) {
        if(viewModel is FlappyViewModel) {
            this.viewModel = viewModel
        }
    }

    override fun initGame(context: Context, listener: GameListener) {
        val minListenedPitch = "C2"
        val maxListenedPitch = "C6"

        pitchRecogniser = PitchRecogniser(context,
            minListenedPitch, maxListenedPitch)

        this.viewModel!!.pitchRecogniser = pitchRecogniser
        //pitchRecogniser.start()
        gameView.setViewModelData(viewModel!!)
        soundPlayer = DefaultSoundPlayerManager(context)
        gameView.setEndListener(listener)

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
