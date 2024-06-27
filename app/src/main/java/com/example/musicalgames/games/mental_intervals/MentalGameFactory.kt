package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GamePackage

class MentalGameFactory : GameFactory {
    override fun getPackages(): List<GamePackage> {
        return listOf(
            GamePackage("Interval to note", 0),
            GamePackage("Note to interval", 1)
        )
    }

    override fun getLevels(pack: GamePackage): List<Level> {
        return if(pack.id == 0) MentalLevels.baseLevels
        else MentalLevels.noteLevels
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun getViewModelType(): Class<out ViewModel> {
        return MentalViewModel::class.java
    }

    override fun getIntentMaker(): GameIntentMaker {
       return MentalViewModel.Companion
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[MentalViewModel::class.java]
        val gameView = MentalView(context)
        gameView.setViewModel(viewModel)
        gameContainer.addView(gameView)

        val gameController = MentalController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(context, gameListener)
        return gameController
    }
}