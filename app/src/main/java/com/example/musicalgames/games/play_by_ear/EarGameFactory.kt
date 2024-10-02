package com.example.musicalgames.games.play_by_ear

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

class EarGameFactory : GameFactory {
    override fun getPackages(): List<GamePackage> {
        return listOf( GamePackage("C major", 0), GamePackage("A minor", 1))
    }

    override fun getLevels(pack: GamePackage): List<Level> {
        return if(pack.id==0)EarPlayLevels.baseLevels else EarPlayLevels.minorLevels
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

    override fun getCustomCreator(context: Context, attrs: AttributeSet): CustomGameCreator {
        TODO("Not yet implemented")
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