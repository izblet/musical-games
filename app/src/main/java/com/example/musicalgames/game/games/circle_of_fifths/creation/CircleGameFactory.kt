package com.example.musicalgames.game.games.circle_of_fifths.creation

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.game.games.circle_of_fifths.CircleView
import com.example.musicalgames.game.games.circle_of_fifths.CircleViewModel
import com.example.musicalgames.game.games.circle_of_fifths.GameLogicCircle
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.game_levels.TaggedLevel

class CircleGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        val level1 = CircleLevel(positionToName = true, minor=false, major=true)
        val tagged1 = TaggedLevel(Game.CIRCLE,1,"Major - position to name", "", level1, isFavourite = false, isCustom = false)
        val level2 = CircleLevel(positionToName = false, minor=false, major=true)
        val tagged2 = TaggedLevel(Game.CIRCLE,2,"Major - name to position", "", level2, isFavourite = false, isCustom = false)
        return listOf(tagged1, tagged2)
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun makeViewModel(level: Level, owner: ViewModelStoreOwner) : GameViewModel {
        if(level !is CircleLevel) {
            throw IllegalArgumentException("level is not of type CircleLevel")
        }
        val gameLogic = GameLogicCircle(level)
        val viewModel = ViewModelProvider(owner)[CircleViewModel::class.java]
        viewModel.setLogic(gameLogic)
        return viewModel
    }

    override fun getCustomCreator(
        context: Context,
        createLevelAction: (Level) -> Unit,
        attrs: AttributeSet?
    ): CustomGameCreator {
        return CircleCustomCreator(context, createLevelAction, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[CircleViewModel::class.java]
        val gameView = CircleView(context, viewModel, activity)
        gameContainer.addView(gameView)

        return viewModel
    }
}