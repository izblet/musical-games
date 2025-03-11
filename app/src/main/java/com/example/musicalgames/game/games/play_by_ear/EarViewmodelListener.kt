package com.example.musicalgames.game.games.play_by_ear

interface EarViewmodelListener {
    fun onNewProblem()
    fun onPlaybackStarted()
    fun onPlaybackFinished()
    fun onRightAnswer()
    fun onWrongAnswer()
}