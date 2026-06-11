package com.example.musicalgames.game.games.flappy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.games.flappy.game_logic.GameEndReason
import com.example.musicalgames.game.games.flappy.game_logic.GameLogic
import com.example.musicalgames.game.games.flappy.graphics.FlappyRenderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FlappyViewModel : ViewModel() {

    lateinit var level: FlappyLevel
        private set

    private var _gameLogic: GameLogic? = null
    private val gameLogic get() = _gameLogic ?: throw IllegalStateException("Game logic not set")

    private var loopJob: Job? = null
    private var pitchLoopJob: Job? = null

    private val frameRateMillis = 1000L / 60

    private val _renderState = MutableStateFlow<FlappyRenderState?>(null)
    val renderState: StateFlow<FlappyRenderState?> = _renderState.asStateFlow()

    val endReason: GameEndReason? get() = gameLogic.endReason

    fun setLevel(level: FlappyLevel) {
        this.level = level
    }

    fun setLogic(logic: GameLogic) {
        _gameLogic = logic
    }

    fun startGameLoop(): Job {
        //pitch recognition can be slow, so it runs in its own loop on a background dispatcher
        //and must not block the render/tick loop below
        pitchLoopJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                gameLogic.pollPitch()
                delay(frameRateMillis)
            }
        }

        val job = viewModelScope.launch {
            while (true) {
                gameLogic.tickFrame()
                _renderState.value = FlappyRenderState(
                    birdShape = gameLogic.getBirdShape(),
                    pipes = gameLogic.getPipeRects(),
                    pipeNotes = gameLogic.getPipeNotes(),
                    score = gameLogic.score,
                    gameEnded = gameLogic.gameEnded
                )
                if (gameLogic.gameEnded) break
                delay(frameRateMillis)
            }
            pitchLoopJob?.cancel()
        }
        loopJob = job
        return job
    }

    fun stopGameLoop() {
        loopJob?.cancel()
        pitchLoopJob?.cancel()
    }

    fun getScore(): Int = gameLogic.score
}
