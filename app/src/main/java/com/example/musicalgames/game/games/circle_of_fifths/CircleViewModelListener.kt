package com.example.musicalgames.game.games.circle_of_fifths

interface CircleViewModelListener {
    fun onRightAnswer()
    fun onWrongAnswer()
    fun onNewQuestion()
    fun onGameEnded()
}