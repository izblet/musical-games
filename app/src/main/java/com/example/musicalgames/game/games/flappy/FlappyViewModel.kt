package com.example.musicalgames.games.flappy

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game.games.flappy.FlappyRenderState
import com.example.musicalgames.game.games.flappy.game_logic.GameLogic
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.utils.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerManager
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FlappyViewModel() : ViewModel() {

    private lateinit var gameLogic: GameLogic
    private var loopJob: Job? = null

    private val frameRateMillis = 1000L / 60

    private val _renderState = MutableStateFlow<FlappyRenderState?>(null)
    val renderState: StateFlow<FlappyRenderState?> = _renderState.asStateFlow()

    fun setGameLogic(gameLogic: GameLogic) {
        this.gameLogic = gameLogic
    }

    fun startGameLoop() {
        if (loopJob?.isActive == true) return
        loopJob = viewModelScope.launch {
            while (isActive) {
                gameLogic.tickFrame()
                _renderState.value = FlappyRenderState(
                    birdShape = gameLogic.getBirdShape(),
                    pipes = gameLogic.getPipeRects(),
                    score = gameLogic.score,
                    gameEnded = gameLogic.gameEnded
                )
                if (gameLogic.gameEnded) break
                delay(frameRateMillis)
            }
        }
    }

    fun stopGameLoop() {
        loopJob?.cancel()
    }

    //--- legacy: TODO remove once the GameLogic-based implementation above replaces FloppyGameView/FlappyGameController ---

    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: Int = Note(ChromaticNote.C, 3).midiCode
    var maxRange: Int = Note(ChromaticNote.C, 4).midiCode
    var root: Int = Note(ChromaticNote.C, 4).midiCode
    var mode: Mode = Mode.IONIAN
    var endAfter: Int = LEN_INF
    var gapPositions: List<Int> = listOf()

    private var soundPlayer: SoundPlayerManager? = null

    fun initSoundPlayer(context: Context) {
        soundPlayer = DefaultSoundPlayerManager(context.applicationContext)
    }

    // TODO: played notes should come from the level (e.g. a "resolution" field or just root)
    fun playKeyEstablishment() {
        val player = soundPlayer ?: return
        viewModelScope.launch {
            player.playNote(minRange, null)
            delay(1000)
            player.playNote(maxRange, null)
            delay(1000)
            player.playNote(root, null)
        }
    }

    fun setLevel(level: Level) {
        //should include a check
        val flappyLevel = level as FlappyLevel

        minRange = flappyLevel.minPitch
        maxRange = flappyLevel.maxPitch
        root = flappyLevel.root
        mode = flappyLevel.mode
        endAfter = flappyLevel.endAfter
        gapPositions = flappyLevel.keyList
    }

}
