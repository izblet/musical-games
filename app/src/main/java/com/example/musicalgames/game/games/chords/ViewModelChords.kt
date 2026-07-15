package com.example.musicalgames.game.games.chords

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.input.ChromaticNoteInputSource
import com.example.musicalgames.game.game_core.input.MicrophoneChromaticNoteInput
import com.example.musicalgames.game.game_core.input.NoteGestureEvent
import com.example.musicalgames.game.game_core.input.RepeatNoteConfirmGesture
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.music_model.Chord
import com.example.musicalgames.music_model.ChromaticNote
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
    private var screenHighlighter: ScreenHighlighter? = null

    var gameplay: GamePlayInstance = GamePlayInstance()
    private var _noteInputSource: ChromaticNoteInputSource? = null
    private val noteInputSource get() = _noteInputSource ?: throw IllegalStateException("Note input source not set")
    //confirms by repeating the question's root note twice in a row instead of a button -
    //works the same regardless of input method, since both flow through the same noteFinished
    private val confirmGesture = RepeatNoteConfirmGesture<ChromaticNote>()

    fun setLogic(logic: GameLogicChords) {
        _gameLogic=logic
    }

    fun setNoteInput(source: ChromaticNoteInputSource) {
        _noteInputSource = source
    }

    fun setBpm(bpm: Long) {
        this.waitTime=2*60*1000/bpm
    }

    fun setScreenHighlighter(highlighter: ScreenHighlighter) {
        screenHighlighter = highlighter
    }

    override fun setViewModel(viewModel: ViewModel) {
        //TODO: should be deleted
    }

    override fun initGame(context: Context, listener: GameListener) {
        this.listener = listener
    }
    private fun onWrongAns(correct: Chord) {
        screenHighlighter?.wrong()
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
        screenHighlighter?.correct()
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
        confirmGesture.setTrigger(question.root)
        val newState = _viewState.value.copy(
            screenMessage = "${question.getName(Random.nextBoolean())}\nPlay root twice to submit",
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

        noteInputSource.start()
        //the gesture needs both ends of each note's lifecycle: a Finished event with no
        //matching Started call on record (e.g. one that began before this game ever started
        //listening) is recognised as orphaned rather than silently counted towards a repeat
        viewModelScope.launch {
            noteInputSource.noteStarted.collect { note -> confirmGesture.onNoteStarted(note) }
        }
        viewModelScope.launch {
            noteInputSource.noteFinished.collect { note ->
                when (val event = confirmGesture.onNoteFinished(note)) {
                    is NoteGestureEvent.NoteSelected -> clickNote(event.note)
                    is NoteGestureEvent.Confirmed -> confirm()
                }
            }
        }
    }

    fun clickNote(note: ChromaticNote) {
        if(!gameLogic.awaitingAnswer())
            return

        gameLogic.addToSelection(note)
        val oldNotes = _viewState.value.highlightedNotes
        _viewState.value = _viewState.value.copy(highlightedNotes = oldNotes + note)
    }

    fun confirm() {
        if(!gameLogic.awaitingAnswer())
            return

        val result = gameLogic.confirm()
        if(result.correct)
            onRightAns()
        else onWrongAns(result.rightAns)
    }

    override fun pauseGame() {
    }

    override fun endGame() {
        _noteInputSource?.stop()
        (_noteInputSource as? MicrophoneChromaticNoteInput)?.release()
    }

    override fun getScore(): Int {
        return 0
    }

    override fun getEndDescription(): String {
        return ""
    }
}