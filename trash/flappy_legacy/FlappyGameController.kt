package com.example.musicalgames.games.flappy

// commented out: superseded by FlappyController, kept temporarily until the new game is verified
/*
import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlappyGameController(private val gameView: FloppyGameView) : GameController {
    private var isGameRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val frameRateMillis = 1000 / 60 // 60 frames per second
    private var birdUpdateJob: Job? = null
    private var viewModel: FlappyViewModel? = null
    private var pitchRecogniser: PitchRecogniser? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var gameEnded = false

    companion object {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    override fun startGame(owner: LifecycleOwner) {
        lifecycleOwner = owner
        viewModel!!.playKeyEstablishment()
        handler.postDelayed({
            pitchRecogniser!!.start()
            isGameRunning = true
            startGameLoop(owner)
        }, 3500)
    }

    override fun pauseGame() {
        stopGameLoop()
        gameView.freezeBird()
        pitchRecogniser!!.stop()
        viewModel!!.playKeyEstablishment()
        handler.postDelayed(::resumeGame, 3000)
    }

    private fun resumeGame() {
        pitchRecogniser!!.start()
        isGameRunning = true
        startGameLoop(lifecycleOwner!!)
    }

    override fun endGame() {
        if (gameEnded) return
        gameEnded = true
        stopGameLoop()
        lifecycleOwner = null
        viewModel!!.pitchRecogniser!!.release()
    }

    override fun getScore(): Int {
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

        pitchRecogniser = PitchRecogniser(context, minListenedPitch, maxListenedPitch)
        this.viewModel!!.pitchRecogniser = pitchRecogniser
        this.viewModel!!.initSoundPlayer(context)
        gameView.setViewModelData(viewModel!!)
        gameView.setEndListener(listener)
    }

    private fun stopGameLoop() {
        isGameRunning = false
        birdUpdateJob?.cancel()
        handler.removeCallbacksAndMessages(null)
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
                    gameView.tickFrame()
                    handler.postDelayed(this, frameRateMillis.toLong())
                }
            }
        })
    }

}
*/
