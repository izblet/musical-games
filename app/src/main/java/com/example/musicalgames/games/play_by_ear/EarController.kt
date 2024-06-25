package com.example.musicalgames.games.play_by_ear

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener

class EarController(private val view: EarView) : GameController {
    override fun registerListener(listener: GameListener) {
        view.registerEndListener(listener)
    }

    override fun unregisterListener(listener: GameListener) {
        //TODO("Not yet implemented")
    }

    override fun setViewModel(viewModel: ViewModel) {
        view.setViewModel(viewModel as EarViewModel)
    }

    override fun initGame(context: Context, listener: GameListener) {
        view.registerEndListener(listener)
    }

    override fun startGame(owner: LifecycleOwner) {
        view.newProblem()
    }

    override fun pauseGame() {
        //TODO("Not yet implemented")
    }

    override fun endGame() {
        //TODO("Not yet implemented")
    }

    override fun getScore(): Int {
        //TODO("Not yet implemented")
        return 0
    }
}