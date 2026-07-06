package com.example.musicalgames.game.games.flappy


import android.view.Choreographer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.games.flappy.game_logic.GameEndReason
import com.example.musicalgames.game.games.flappy.game_logic.GameLogic
import com.example.musicalgames.game.games.flappy.graphics.FlappyRenderState
import com.example.musicalgames.game_activity.ScreenHighlighter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FlappyViewModel : ViewModel() {

    lateinit var level: FlappyLevel
        private set

    private var _gameLogic: GameLogic? = null
    private val gameLogic get() = _gameLogic ?: throw IllegalStateException("Game logic not set")

    private var refreshViewJob: Job? = null

    private val controllerUpdateMS = 1000L / 60
    

    //after this much real time with no tick, stop
    //scaling pipe movement further
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

    private var screenHighlighter: ScreenHighlighter? = null

    fun setScreenHighlighter(highlighter: ScreenHighlighter) {
        screenHighlighter = highlighter
    }


    private suspend fun awaitFrame(): Long = suspendCancellableCoroutine { cont ->
        val callback = Choreographer.FrameCallback { frameTimeNanos ->
            cont.resume(frameTimeNanos)
        }
        Choreographer.getInstance().postFrameCallback(callback)
        cont.invokeOnCancellation {
            Choreographer.getInstance().removeFrameCallback(callback)
        }
    }

    fun startGameLoop(owner: LifecycleOwner): Job {

        //redraw loop
        var lastFrameTimeNanos: Long? = null
        var previousScore = gameLogic.score

        val refreshView = owner.lifecycleScope.launch {

            while(true) {
                val frameTimeNanos = awaitFrame()
                val deltaTimeSeconds = lastFrameTimeNanos?.let {
                    ((frameTimeNanos - it) / 1_000_000_000.0).coerceAtMost(maxDeltaTimeSeconds)
                } ?: 0.0
                lastFrameTimeNanos = frameTimeNanos

                gameLogic.tickBird()
                gameLogic.tickFrame(deltaTimeSeconds)

                if (gameLogic.score > previousScore) {
                    //screenHighlighter?.correct()
                    previousScore = gameLogic.score
                }
                if (gameLogic.gameEnded && gameLogic.endReason == GameEndReason.COLLISION) {
                    screenHighlighter?.wrong()
                }

                _renderState.value = FlappyRenderState(
                    birdShape = gameLogic.getBirdShape(),
                    pipes = gameLogic.getPipeRects(),
                    pipeNotes = gameLogic.getPipeNotes(),
                    score = gameLogic.score,
                    gameEnded = gameLogic.gameEnded
                )

                if (gameLogic.gameEnded) break
            }
        }
        refreshViewJob = refreshView

        return refreshView
    }
    fun clear() {
        _renderState.value = null
    }

    fun stopGameLoop() {
        refreshViewJob?.cancel()
    }

    fun getScore(): Int = gameLogic.score
}
