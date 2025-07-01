package com.example.musicalgames.game.games.mental_intervals.creation

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.mental_intervals.MentalController
import com.example.musicalgames.games.mental_intervals.MentalLevels
import com.example.musicalgames.games.mental_intervals.MentalView
import com.example.musicalgames.games.mental_intervals.MentalViewModel
import com.example.musicalgames.main_app.game_levels.TaggedLevel

class MentalGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return MentalLevels.intervalNoteLevels
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun makeViewModel(level: Level, owner: ViewModelStoreOwner) : GameViewModel {
        val viewModel: MentalViewModel = ViewModelProvider(owner)[MentalViewModel::class.java]
        viewModel.setLevel(level)
        return viewModel
    }


    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return MentalCustomCreator(context, createLevelAction, attrs)
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