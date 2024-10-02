package com.example.musicalgames.games.flappy

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.util.Log
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

class FlappyGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<Level> {
        val levelDao: LevelDao = GameDatabase.getDatabase(context).flappyLevelDao()
        val baseLevels = levelDao.getBaseLevels()
        baseLevels.forEach { level ->  Log.d("level", level.name)}
        Log.d("level", baseLevels.size.toString())
        return when (pack) {
            GamePackage.PREDEFINED -> levelDao.getBaseLevels()
            GamePackage.CUSTOM -> levelDao.getCustomLevels()
            else -> levelDao.getFavouriteLevels()
        }
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

    override fun getCustomCreator(context: Context, attrs: AttributeSet): CustomGameCreator {
        TODO("not implemented yet")
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