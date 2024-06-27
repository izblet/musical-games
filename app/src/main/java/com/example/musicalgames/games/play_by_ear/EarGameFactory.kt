package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.game_activity.IntentSettable
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GamePackage

class EarGameFactory : GameFactory {
    override fun getPackages(): List<GamePackage> {
        return listOf( GamePackage("Levels", 0))
    }

    override fun getLevels(pack: Int): List<Level> {
        return EarPlayLevels.baseLevels
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun getViewModelType(): Class<out ViewModel> {
        return EarViewModel::class.java
    }

    override fun getIntentMaker(): GameIntentMaker {
       return EarViewModel.Companion
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[EarViewModel::class.java]
        val gameView = EarView(context, null)
        gameContainer.addView(gameView)

        val gameController = EarController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(context, gameListener)

        return gameController
    }
}