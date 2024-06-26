package com.example.musicalgames.games.mental_intervals

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener

class MentalController(private val view: MentalView) : GameController {
    override fun registerListener(listener: GameListener) {
        //TODO("Not yet implemented")
    }

    override fun unregisterListener(listener: GameListener) {
        //TODO("Not yet implemented")
    }

    override fun setViewModel(viewModel: ViewModel) {
        //TODO("Not yet implemented")
    }

    override fun initGame(context: Context, listener: GameListener) {
        //TODO("Not yet implemented")
    }

    override fun startGame(owner: LifecycleOwner) {
       //TODO("Not yet implemented")
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