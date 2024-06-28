package com.example.musicalgames.games.flappy

import android.Manifest
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

class FlappyGameFactory : GameFactory {
    override fun getPackages(): List<GamePackage> {
        return listOf(GamePackage("C major", 0),
            GamePackage("A minor", 1))
    }

    override fun getLevels(pack: GamePackage): List<Level> {
        if(pack.id==0) return FlappyLevels.baseLevels
        else return FlappyLevels.minorLevels
    }

    override fun getPermissions(): Array<String> {
        return arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    override fun getViewModelType(): Class<out ViewModel> {
        return FlappyViewModel::class.java
    }

    override fun getIntentMaker(): GameIntentMaker {
        return FlappyViewModel.Companion

    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[FlappyViewModel::class.java]
        val gameView = FloppyGameView(context)
        gameContainer.addView(gameView)

        val gameController = FlappyGameController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(context, gameListener)

        return gameController
    }

}