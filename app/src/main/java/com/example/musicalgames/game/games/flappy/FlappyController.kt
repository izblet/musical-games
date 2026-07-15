package com.example.musicalgames.game.games.flappy

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.games.flappy.game_logic.GameEndReason
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.game.game_core.input.PitchSource
import com.example.musicalgames.utils.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlappyController(
    private val viewModel: FlappyViewModel,
    private val pitchRecogniser: PitchSource
) : GameController {

    private var soundPlayer: SoundPlayerManager? = null
    private var gameListener: GameListener? = null

    override fun setViewModel(viewModel: ViewModel) {
        // the view model is provided through the constructor instead
        //TODO: should be removed from the base class
    }

    override fun initGame(context: Context, listener: GameListener) {
        gameListener = listener
        soundPlayer = DefaultSoundPlayerManager(context.applicationContext)
    }

    override fun startGame(owner: LifecycleOwner) {
        val level = viewModel.level
        val player = soundPlayer

        owner.lifecycleScope.launch {
            viewModel.setKeyEstablishmentMessage("lowest note")
            player?.playNote(level.minPitch)
            delay(1000)
            viewModel.setKeyEstablishmentMessage("highest note")
            player?.playNote(level.maxPitch)
            delay(1000)
            player?.let { viewModel.keyEstablishmentPlayer?.play(it) }
        }

        owner.lifecycleScope.launch {
            delay(3500)
            pitchRecogniser.start()
            delay(1000)
            viewModel.startGameLoop(owner).join()
            if (viewModel.endReason == GameEndReason.COLLISION) {
                delay(ScreenHighlighter.FLASH_DURATION_MS)
            }
            gameListener?.onGameEnded()

        }
    }


    override fun pauseGame() {
        // TODO: deferred until pause button wiring
    }

    override fun endGame() {
        viewModel.stopGameLoop()
        pitchRecogniser.release()
    }

    override fun getScore(): Int = viewModel.getScore()

    override fun getEndDescription(): String {
        return when (viewModel.endReason) {
            GameEndReason.COLLISION -> "You hit a pipe!"
            GameEndReason.SCORE_REACHED -> "You reached the end!"
            null -> ""
        }
    }
}
