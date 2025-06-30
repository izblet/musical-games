package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener

class CircleGameController : GameController {
    private var _viewModel: CircleViewModel? = null
    private val viewModel get() = _viewModel!!

    override fun setViewModel(viewModel: ViewModel) {
        if(viewModel is CircleViewModel) {
           _viewModel = viewModel
        } else {
            throw IllegalArgumentException("CircleGameController: wrong type of viewmodel")
        }
    }

    override fun initGame(context: Context, listener: GameListener) {
    }

    override fun startGame(owner: LifecycleOwner) {
    }

    override fun pauseGame() {
    }

    override fun endGame() {
    }

    override fun getScore(): Int {
        return viewModel.score
    }

    override fun getEndDescription(): String {
        return "game has ended"
    }
}