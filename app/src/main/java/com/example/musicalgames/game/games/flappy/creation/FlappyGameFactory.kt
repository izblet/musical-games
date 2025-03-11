package com.example.musicalgames.game.games.flappy.creation

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.flappy.FlappyGameController
import com.example.musicalgames.games.flappy.FlappyLevelDao
import com.example.musicalgames.games.flappy.FlappyViewModel
import com.example.musicalgames.games.flappy.FloppyGameView

class FlappyGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<Level> {
        val flappyLevelDao: FlappyLevelDao = GameDatabase.getDatabase(context).flappyLevelDao()

        return when (pack) {
            GamePackage.PREDEFINED -> flappyLevelDao.getBaseLevels()
            GamePackage.CUSTOM -> flappyLevelDao.getCustomLevels()
            else -> flappyLevelDao.getFavouriteLevels()
        }
    }

    override fun getPermissions(): Array<String> {
        return arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    override fun getViewModelType(): Class<out ViewModel> {
        return FlappyViewModel::class.java
    }

    override fun getIntentMaker(): GameIntentMaker {
        return FlappyViewModel

    }

    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return FlappyCustomCreator(context, createLevelAction, attrs)
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