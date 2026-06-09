package com.example.musicalgames.game.games.mental_intervals

import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Interval

interface MentalViewmodelListener {
    fun onNewProblem(interval: Interval, questionNote: ChromaticNote)
    fun onNewProblem(questionNote: ChromaticNote, note: ChromaticNote)
    fun onRightAnswer()
    fun onWrongAnswer(correctAns: Interval)
    fun onWrongAnswer(correctAns: ChromaticNote)
}