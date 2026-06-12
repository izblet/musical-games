package com.example.musicalgames.game.games.flappy

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.games.flappy.game_logic.GameEndReason
import com.example.musicalgames.game.games.flappy.game_logic.GameLogic
import com.example.musicalgames.game.games.flappy.graphics.FlappyRenderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlappyViewModel : ViewModel() {

    lateinit var level: FlappyLevel
        private set

    private var _gameLogic: GameLogic? = null
    private val gameLogic get() = _gameLogic ?: throw IllegalStateException("Game logic not set")

    private var loopJob: Job? = null
    private var pitchLoopJob: Job? = null

    private val frameRateMillis = 1000L / 60

    //after this much real time with no tick (app backgrounded, GC pause, debugger...), stop
    //scaling pipe movement further - avoids a big visual "jump"
    private val maxDeltaTimeSeconds = 0.1

    private val _renderState = MutableStateFlow<FlappyRenderState?>(null)
    val renderState: StateFlow<FlappyRenderState?> = _renderState.asStateFlow()

    val endReason: GameEndReason? get() = gameLogic.endReason

    fun setLevel(level: FlappyLevel) {
        this.level = level
    }

    fun setLogic(logic: GameLogic) {
        _gameLogic = logic
    }

    private val handler = Handler(Looper.getMainLooper())
    fun startBirdLoop(owner: LifecycleOwner): Job {

        //redraw loop
        var lastFrameTimeNanos: Long? = null
        handler.post(object : Runnable {
            override fun run() {
                Log.d("LOOPS", "refresh loop")
                val frameTimeNanos = System.nanoTime()
                val deltaTimeSeconds = lastFrameTimeNanos?.let {
                    ((frameTimeNanos - it) / 1_000_000_000.0).coerceAtMost(maxDeltaTimeSeconds)
                } ?: 0.0
                lastFrameTimeNanos = frameTimeNanos

                gameLogic.tickFrame(deltaTimeSeconds)
                _renderState.value = FlappyRenderState(
                    birdShape = gameLogic.getBirdShape(),
                    pipes = gameLogic.getPipeRects(),
                    pipeNotes = gameLogic.getPipeNotes(),
                    score = gameLogic.score,
                    gameEnded = gameLogic.gameEnded
                )
                //TODO: remove magic framepersecond number
                if(!gameLogic.gameEnded)
                    handler.postDelayed(this, 1000/60)

            }
        })

        //bird loop
        val job = owner.lifecycleScope.launch {
            while (true) {
                //Dispatchers.Default is for CPU-bound heavy processes, runs on a different thread
                withContext(Dispatchers.Default) {
                    gameLogic.tickBird()
                }
                if (gameLogic.gameEnded) break
                delay(frameRateMillis)
            }
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
