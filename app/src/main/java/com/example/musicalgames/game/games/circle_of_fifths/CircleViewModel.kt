package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.input.ChromaticNoteInputSource
import com.example.musicalgames.game.game_core.input.MicrophoneChromaticNoteInput
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.CircleOfFifths
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.music_model.display.ModeSpelling
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CircleViewState(
    val showKeyboard: Boolean = false,
    val question: String? = null,
    val questionMode: Mode? = null,
    val highlightedNote: Int? = null,
    val screenCommandMessage: String? = null
)

class CircleViewModel: ViewModel(), GameController {
    private val _viewState = MutableStateFlow(CircleViewState())
    val viewState: StateFlow<CircleViewState> = _viewState.asStateFlow()

    private var _gameLogic: GameLogicCircle? = null
    private val gameLogic get() = _gameLogic ?: throw IllegalStateException("Game logic not set")

    private var gameListener: GameListener? = null
    private var screenHighlighter: ScreenHighlighter? = null

    private var waitLen:Long =1000

    //guards against gameLogic.answer() being invoked a second time before the next question
    //is actually rendered - gameLogic.awaitingAnswer() alone doesn't gate this, since it stays
    //true for the whole (infinite-mode) game including the post-answer feedback delay
    private var answered = false

    var gameplay: GamePlayInstance = GamePlayInstance()

    private var _noteInputSource: ChromaticNoteInputSource? = null
    private val noteInputSource get() = _noteInputSource ?: throw IllegalStateException("Note input source not set")
    fun setNoteInput(source: ChromaticNoteInputSource) { _noteInputSource = source }

    fun setLogic(logic: GameLogicCircle) {
        _gameLogic = logic
    }

    fun setScreenHighlighter(highlighter: ScreenHighlighter) {
        screenHighlighter = highlighter
    }

    fun setBpm(bpm: Long) {
        val beatLen = 60*1000/bpm
        waitLen = 2*beatLen
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

        noteInputSource.start()
        viewModelScope.launch {
            noteInputSource.noteStarted.collect { note -> clickNote(note) }
            noteInputSource.noteFinished.collect { note -> delayAndNext() }
        }
    }
    private fun newQuestionCircleToNote() {
        answered = false
        val newState = _viewState.value.copy(
            showKeyboard = true,
            question = ModeSpelling.common(gameLogic.questionMode),
            questionMode = gameLogic.questionMode,
            highlightedNote = gameLogic.questionNoteIndex,
            screenCommandMessage = null
        )
        _viewState.value = newState
    }

    private fun newQuestionNoteToCircle() {
        answered = false
        val noteName = NoteSpelling.spell(gameLogic.questionNote, SpellingPreference.MIXED)
        val modeName = ModeSpelling.common(gameLogic.questionMode)
        val newState = _viewState.value.copy(
            showKeyboard = false,
            question = "$noteName\n$modeName",
            questionMode = gameLogic.questionMode,
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
            delay(waitLen)
            update()
        }
    }

    fun clickCircle(index: Int) {
        val note = CircleOfFifths.atIndex(index, gameLogic.questionMode)

        if(!gameLogic.awaitingAnswer() || answered)
            return

        if(!gameLogic.isCircleToNote()) {
            answered = true
            val answerResult = gameLogic.answer(note)
            if (answerResult.correct) screenHighlighter?.correct() else screenHighlighter?.wrong()

            val screenCommandMessage = if(answerResult.correct) {
                "Good"
            } else {
                "Wrong"
            }

            val highlightedNote = if(answerResult.correct) {
                null
            } else {
                answerResult.rightAnsIndex
            }

            val newState :CircleViewState = _viewState.value.copy(screenCommandMessage = screenCommandMessage, highlightedNote = highlightedNote)

            _viewState.value = newState
        }
    }

    fun clickNote(note: ChromaticNote) {
        if(!gameLogic.awaitingAnswer() || answered)
            return

        if(gameLogic.isCircleToNote()) {
            answered = true
            val answerResult = gameLogic.answer(note)
            if (answerResult.correct) screenHighlighter?.correct() else screenHighlighter?.wrong()
            val screenCommandMessage = if(answerResult.correct) {
                "Good"
            } else {
                "Wrong, the right answer was ${NoteSpelling.spell(answerResult.rightAnsNote!!, SpellingPreference.MIXED)}"
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
        _noteInputSource?.stop()
        (_noteInputSource as? MicrophoneChromaticNoteInput)?.release()
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