package com.example.musicalgames.game.games.circle_of_fifths

import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Interval

data class AnswerResult (
    val correct: Boolean,
    val rightAns: ChromaticNote?
)

class GameLogicCircle (private val level: CircleLevel) {

    private var _gameStarted = false
    private var _gameEnded = false
    private var _questionNote = ChromaticNote.C
    private var _rightAnsNum = 0
    private var _wrongAnsNum = 0

    val gameStarted get() = _gameStarted
    val gameEnded get() = _gameEnded
    val questionNote get() = _questionNote
    val rightAnsNum get() = _rightAnsNum
    val wrongAnsNum get() = _wrongAnsNum


    //TODO: this is very very temporary - breaks semantics down the line
    fun getNoteTransposed(resultNote: ChromaticNote) : ChromaticNote {
        val interval = Interval.fromChromaticNotes(level.modeRootInCMajor.chromaticNote, ChromaticNote.C)
        return resultNote.transpose(interval)
    }


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
    }

    fun answer(answerNote: ChromaticNote) : AnswerResult {
        if(!awaitingAnswer()) {
            return AnswerResult(correct = false, rightAns = null)
        }
        if(answerNote== getNoteTransposed(questionNote)) {
            _rightAnsNum++
            val receipt = AnswerResult(correct = true, rightAns = getNoteTransposed(questionNote))
            nextQuestion()
            return receipt
        }
        else {
            //only in infinite mode
            _wrongAnsNum++
            val receipt = AnswerResult(correct = false, rightAns = getNoteTransposed(questionNote))
            nextQuestion()
            return receipt
        }
    }

}