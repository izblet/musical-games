package com.example.musicalgames.game.games.circle_of_fifths

import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.game_activity.Level

class CircleViewModel: ViewModel(), GameViewModel {
    private var _level: CircleLevel? = null
    private val level get() = _level!!

    private var _score = 0
    val score get() = _score

    override fun setLevel(level: Level) {
        if(level is CircleLevel) {
            _level = level
        } else {
            throw IllegalArgumentException("CircleViewModel: level is of wrong type")
        }
    }
}