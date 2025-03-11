package com.example.musicalgames.game.games.mental_intervals

import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval

interface MentalViewmodelListener {
    fun onNewProblem(interval: Interval, questionNote: ChromaticNote)
    fun onNewProblem(questionNote: ChromaticNote, note: ChromaticNote)
    fun onRightAnswer()
    fun onWrongAnswer(correctAns: Interval)
    fun onWrongAnswer(correctAns: ChromaticNote)
}