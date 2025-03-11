package com.example.musicalgames.game.games.play_by_ear.creation

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
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.play_by_ear.EarController
import com.example.musicalgames.games.play_by_ear.EarPlayLevels
import com.example.musicalgames.games.play_by_ear.EarView
import com.example.musicalgames.games.play_by_ear.EarViewModel

class EarGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<Level> {
        return if(pack == GamePackage.PREDEFINED) EarPlayLevels.baseLevels else EarPlayLevels.minorLevels
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun getViewModelType(): Class<out ViewModel> {
        return EarViewModel::class.java
    }

    override fun getIntentMaker(): GameIntentMaker {
       return EarViewModel
    }

    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return EarCreatorView(context, createLevelAction, attrs)
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