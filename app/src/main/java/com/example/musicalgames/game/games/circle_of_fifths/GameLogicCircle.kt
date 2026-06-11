package com.example.musicalgames.game.games.circle_of_fifths

import com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Mode

data class AnswerResult (
    val correct: Boolean,
    val rightAnsIndex: Int?,
    val rightAnsNote: ChromaticNote?
)

class GameLogicCircle (private val level: CircleLevel) {

    private var _gameStarted = false
    private var _gameEnded = false
    private var _questionNote = ChromaticNote.C
    private var _questionMode = Mode.IONIAN
    private var _questionNoteIndex = 0
    private var _rightAnsNum = 0
    private var _wrongAnsNum = 0

    val gameStarted get() = _gameStarted
    val gameEnded get() = _gameEnded
    val questionNote get() = _questionNote
    val rightAnsNum get() = _rightAnsNum
    val wrongAnsNum get() = _wrongAnsNum
    val questionMode get() = _questionMode
    val questionNoteIndex get() = _questionNoteIndex


    fun isCircleToNote(): Boolean {
        return level.positionToName
    }
    fun awaitingAnswer(): Boolean {
        return gameStarted && (!gameEnded)
    }

    fun startGame() {
        if(!gameStarted) {
            _gameStarted = true
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        _questionNote = ChromaticNote.entries.random()
        _questionMode = level.modes.random()
        _questionNoteIndex = CircleOfFifthsPalette.noteIndex(_questionNote, _questionMode)
    }

    fun answer(answerNote: ChromaticNote) : AnswerResult {
        if(!awaitingAnswer()) {
            return AnswerResult(correct = false, rightAnsIndex = null, rightAnsNote = null)
        }
        if(answerNote== questionNote) {
            _rightAnsNum++
            val receipt = AnswerResult(correct = true, rightAnsIndex = questionNoteIndex, rightAnsNote = questionNote)
            nextQuestion()
            return receipt
        }
        else {
            //only in infinite mode
            _wrongAnsNum++
            val receipt = AnswerResult(correct = false, rightAnsIndex = questionNoteIndex, rightAnsNote = questionNote)
            nextQuestion()
            return receipt
        }
    }

}