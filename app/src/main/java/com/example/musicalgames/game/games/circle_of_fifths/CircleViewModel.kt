package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game

class CircleViewModel(): ViewModel(), GameViewModel, GameController {
    private var _level: CircleLevel? = null
    private val level get() = _level!!

    private var _gameLogic: GameLogicCircle? = null
    private val gameLogic get() = _gameLogic!!

    private var _score = 0

    override fun setLevel(level: Level) {
        if(level is CircleLevel) {
            _level = level
        } else {
            throw IllegalArgumentException("CircleViewModel: level is of wrong type")
        }
    }
    fun setLogic(logic: GameLogicCircle) {
        _gameLogic = logic
    }

    override fun setViewModel(viewModel: ViewModel) {
        //TODO:this is a legacy function, do remove it once you sort everything out
    }

    override fun initGame(context: Context, listener: GameListener) {
        TODO("Not yet implemented")
    }

    override fun startGame(owner: LifecycleOwner) {
        TODO("Not yet implemented")
    }

    override fun pauseGame() {
        TODO("Not yet implemented")
    }

    override fun endGame() {
        TODO("Not yet implemented")
    }

    override fun getScore(): Int {
        TODO("Not yet implemented")
    }

    override fun getEndDescription(): String {
        TODO("Not yet implemented")
    }
}