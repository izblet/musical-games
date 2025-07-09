package com.example.musicalgames.game.games.chords

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.utils.Chord
import com.example.musicalgames.utils.ChromaticNote
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ViewModelChords(): ViewModel(), GameController {
    data class ViewState(
        val screenMessage: String? = null,
        val highlightedNotes: Set<ChromaticNote> = setOf()
    )
    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    private var listener : GameListener?=null
    private var waitTime : Long = 1000
    private val wrongWaitMultiplier: Long = 3
    private var _gameLogic: GameLogicChords? = null
    private val gameLogic get() = _gameLogic ?: throw IllegalStateException("Game logic not set")


    fun setLogic(logic: GameLogicChords) {
        _gameLogic=logic
    }

    fun setBpm(bpm: Long) {
        this.waitTime=2*60*1000/bpm
    }

    override fun setViewModel(viewModel: ViewModel) {
        //TODO: should be deleted
    }

    override fun initGame(context: Context, listener: GameListener) {
        this.listener = listener
    }
    private fun onWrongAns(correct: Chord) {
        val newState = ViewState(
            screenMessage = "Wrong",
            highlightedNotes = correct.getChromaticNotes().toSet()
        )
        _viewState.value = newState
        viewModelScope.launch {
            delay(waitTime*wrongWaitMultiplier)
            newQuestion()
        }
    }
    private fun onRightAns() {
        val newState = ViewState(
            screenMessage = "Correct",
            highlightedNotes = setOf()
        )
        _viewState.value = newState
        viewModelScope.launch {
            delay(waitTime)
            newQuestion()
        }
    }

    private fun newQuestion() {
        val question = gameLogic.newQuestion()
        val newState = _viewState.value.copy(
            screenMessage = question.getName(Random.nextBoolean()),
            highlightedNotes = setOf()
        )
        _viewState.value = newState
    }

    override fun startGame(owner: LifecycleOwner) {
        gameLogic.startGame()
        if(!gameLogic.gameStarted) {
            throw IllegalStateException("Failed to start the game")
        } else {
          newQuestion()
        }
    }

    fun clickNote(note: ChromaticNote) {
        if(!gameLogic.awaitingAnswer())
            return

        val result = gameLogic.addToSelection(note)
        val newState: ViewState
        if(!result.correct)
            onWrongAns(result.rightAns)

        else {
            val oldnotes = _viewState.value.highlightedNotes ?: listOf()
            newState = _viewState.value.copy(
                highlightedNotes = (oldnotes+note).toSet()
            )
            _viewState.value = newState
        }
    }

    fun confirm() {
       val result = gameLogic.confirm()
        if(result.correct)
            onRightAns()
        else onWrongAns(result.rightAns)
    }

    override fun pauseGame() {
    }

    override fun endGame() {
    }

    override fun getScore(): Int {
        return 0
    }

    override fun getEndDescription(): String {
        return ""
    }
}