package com.example.musicalgames.games.mental_intervals

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener

class MentalController(private val view: MentalView) : GameController {

    override fun setViewModel(viewModel: ViewModel) {
        if(viewModel !is MentalViewModel) {
            throw Exception("Viewmodel is of wrong type")
        }
        view.setConstraint(viewModel.maxInterval)
        //TODO("Not yet implemented")
    }

    override fun initGame(context: Context, listener: GameListener) {
        view.registerListener(listener)
    }

    override fun startGame(owner: LifecycleOwner) {
        view.generateQuestion()
    }

    override fun pauseGame() {
        //TODO("Not yet implemented")
    }

    override fun endGame() {
        //TODO("Not yet implemented")
    }

    override fun getScore(): Int {
        return view.score
    }
}