package com.example.musicalgames.game.games.flappy

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.games.flappy.game_logic.GameEndReason
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.utils.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerManager
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlappyController(
    private val viewModel: FlappyViewModel,
    private val pitchRecogniser: PitchRecogniser
) : GameController {

    private var soundPlayer: SoundPlayerManager? = null
    private var gameListener: GameListener? = null

    override fun setViewModel(viewModel: ViewModel) {
        // the view model is provided through the constructor instead
    }

    override fun initGame(context: Context, listener: GameListener) {
        gameListener = listener
        soundPlayer = DefaultSoundPlayerManager(context.applicationContext)
    }

    override fun startGame(owner: LifecycleOwner) {
        val level = viewModel.level
        val player = soundPlayer

        owner.lifecycleScope.launch {
            player?.playNote(level.minPitch)
            delay(1000)
            player?.playNote(level.maxPitch)
            delay(1000)
            player?.playNote(level.root)
        }

        owner.lifecycleScope.launch {
            delay(3500)
            pitchRecogniser.start()
            viewModel.startGameLoop().join()
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
