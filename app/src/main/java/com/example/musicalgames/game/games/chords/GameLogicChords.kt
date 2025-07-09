package com.example.musicalgames.game.games.chords

import com.example.musicalgames.utils.Chord
import com.example.musicalgames.utils.ChromaticNote

class GameLogicChords (private val rootNotes: Set<ChromaticNote>, private val extensions: Set<Chord.Companion.Extension?>, private val qualities: Set<Chord.Companion.Quality>) {
    data class AnswerResult(val correct: Boolean, val rightAns: Chord)

    private var _gameStarted = false
    private var _gameEnded = false
    private var awaitingAns = false
    private var _rightAnsNum = 0
    private var _wrongAnsNum = 0
    private var _questionChord: Chord? = null
    private val answerNotes: MutableSet<ChromaticNote> = mutableSetOf()
    private val availableQuestions: List<Chord>

    val gameStarted get() = _gameStarted
    val gameEnded get() = _gameEnded
    val questionChord get() = _questionChord

    init{
       availableQuestions = generateQuestions()
    }

    fun awaitingAnswer(): Boolean {
        return awaitingAns
    }

    private fun generateQuestions(): List<Chord> {
        val result: MutableList<Chord> = mutableListOf()
        for(rootnote in rootNotes) {
            for(quality in qualities) {
                for(extension in Chord.validExtensions[quality]!!) {
                    if(extension in extensions)
                       result.add(Chord(rootnote, quality, extension))
                }
            }
        }
        return result
    }

    private fun getRandomChord(): Chord {
        return availableQuestions.random()
    }

    fun newQuestion() : Chord {
        awaitingAns=true
        answerNotes.clear()
        _questionChord = getRandomChord()
        return _questionChord?: throw IllegalStateException("problem chord is null")
    }
    fun startGame() {
        _gameStarted=true
    }

    fun confirm(): AnswerResult {
        val question = _questionChord ?: throw IllegalStateException("There is no question to answer")
        val notes = question.getChromaticNotes().toSet()

        if(answerNotes.size!=notes.size) {
            return AnswerResult(false, question)
        }

        for(note in notes) {
            if(!answerNotes.contains(note)) {
                _wrongAnsNum++
                return AnswerResult(false, question)
            }
        }
        val result = AnswerResult(true, question)
        _rightAnsNum++

        awaitingAns=false
        return result
    }

    fun addToSelection(note: ChromaticNote): AnswerResult {
        answerNotes.add(note)
        val question = _questionChord ?: throw IllegalStateException("There is no question to answer")
        if(!question.getChromaticNotes().contains(note)) {
           val result=AnswerResult(false, question)
            _wrongAnsNum++
            awaitingAns=false
            return result
        }
        else {
            return AnswerResult(true, question)
        }
    }
}