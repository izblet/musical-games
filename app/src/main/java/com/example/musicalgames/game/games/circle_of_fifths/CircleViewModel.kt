package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.ChromaticNote
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CircleViewState(
    val showKeyboard: Boolean = false,
    val question: String? = null,
    val highlightedNote: ChromaticNote? = null,
    val screenCommandMessage: String? = null
)

class CircleViewModel(): ViewModel(), GameViewModel, GameController {
    private val _viewState = MutableStateFlow(CircleViewState())
    val viewState: StateFlow<CircleViewState> = _viewState.asStateFlow()

    private var _level: CircleLevel? = null
    private val level get() = _level!!

    private var _gameLogic: GameLogicCircle? = null
    private val gameLogic get() = _gameLogic!!

    private var gameListener: GameListener? = null

    override fun setLevel(level: Level) {
        if(level is CircleLevel) {
            _level = level
        } else {
            throw IllegalArgumentException("CircleViewModel: level is of wrong type")
        }
    }
    fun setLogic(logic: GameLogicCircle) {
        _gameLogic = logic
    }


    override fun initGame(context: Context, listener: GameListener) {
        gameListener = listener
    }

    override fun startGame(owner: LifecycleOwner) {
        gameLogic.startGame()
        if(gameLogic.awaitingAnswer()) {
           update()
        } else {
           throw IllegalStateException("Game failed to start or game length 0")
        }

    }
    private fun newQuestionCircleToNote() {
        val newState = _viewState.value.copy(
            showKeyboard = true,
            question = null,
            highlightedNote = gameLogic.questionNote,
            screenCommandMessage = null
        )
        _viewState.value = newState
    }

    private fun newQuestionNoteToCircle() {
        val newState = _viewState.value.copy(
            showKeyboard = false,
            question = CircleOfFifthsPalette.noteName(gameLogic.questionNote,major=true),
            highlightedNote = null,
            screenCommandMessage = null
        )
        _viewState.value = newState
    }

    private fun update() {
        if(gameLogic.gameEnded) {
            gameListener?.onGameEnded()
        } else if(gameLogic.gameStarted && gameLogic.awaitingAnswer()) {

            if(gameLogic.isCircleToNote()) {
                newQuestionCircleToNote()
            } else {
                newQuestionNoteToCircle()
            }
        }
    }

    private fun delayAndNext() {
        viewModelScope.launch {
            delay(3000)
            update()
        }
    }

    fun clickCircle(note: ChromaticNote) {
        if(!gameLogic.awaitingAnswer())
            return

        if(!gameLogic.isCircleToNote()) {
            val answerResult = gameLogic.answer(note)

            val screenCommandMessage = if(answerResult.correct) {
                "Good"
            } else {
                "Wrong, this is the right answer:"
            }

            val highlightedNote = if(answerResult.correct) {
                null
            } else {
                answerResult.rightAns
            }

            val newState :CircleViewState = _viewState.value.copy(screenCommandMessage = screenCommandMessage, highlightedNote = highlightedNote)

            _viewState.value = newState
            delayAndNext()

        }
    }

    fun clickNote(note: ChromaticNote) {
        if(!gameLogic.awaitingAnswer())
            return

        if(gameLogic.isCircleToNote()) {
            val answerResult = gameLogic.answer(note)
            val screenCommandMessage = if(answerResult.correct) {
                "Good"
            } else {
                "Wrong, the right answer was ${answerResult.rightAns}"
            }

            val newState :CircleViewState = _viewState.value.copy(screenCommandMessage = screenCommandMessage)
            _viewState.value = newState
            delayAndNext()
        }

    }

    override fun pauseGame() {
        //TODO("Not yet implemented")
    }

    override fun endGame() {
        //TODO("Not yet implemented")
    }

    override fun getScore(): Int {
        //TODO: Temporary, actually score should be a string probably in the parent class
        return gameLogic.rightAnsNum
    }

    override fun getEndDescription(): String {
        //TODO("Not yet implemented")
        return ""
    }
    override fun setViewModel(viewModel: ViewModel) {
        //TODO:this is a legacy function, do remove it once you sort everything out
    }
}