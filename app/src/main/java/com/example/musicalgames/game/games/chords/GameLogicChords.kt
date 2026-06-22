package com.example.musicalgames.game.games.chords

import com.example.musicalgames.music_model.Chord
import com.example.musicalgames.music_model.ChromaticNote

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

        val correct = answerNotes == notes
        awaitingAns = false
        if (correct) {
            _rightAnsNum++
        } else {
            _wrongAnsNum++
        }
        return AnswerResult(correct, question)
    }

    //correctness is judged entirely on confirm() now, not per note - a wrong or incomplete
    //selection just stays selected until the player confirms
    fun addToSelection(note: ChromaticNote) {
        answerNotes.add(note)
    }
}