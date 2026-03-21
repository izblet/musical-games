package com.example.musicalgames.game.games.circle_of_fifths.creation

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.game.games.circle_of_fifths.CircleView
import com.example.musicalgames.game.games.circle_of_fifths.CircleViewModel
import com.example.musicalgames.game.games.circle_of_fifths.GameLogicCircle
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Mode

class CircleGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        val levelMajor = CircleLevel(positionToName = false, listOf(Mode.IONIAN))
        val levelMinor = CircleLevel(positionToName = false, listOf(Mode.AEOLIAN))
        val levelDorian = CircleLevel(positionToName = false, listOf(Mode.DORIAN))
        val levelMixolydian = CircleLevel(positionToName = false, listOf(Mode.MIXOLYDIAN))
        val levelLydian = CircleLevel(positionToName = false, listOf(Mode.LYDIAN))

        val levelMajorMinor = CircleLevel(positionToName = false, listOf(Mode.IONIAN, Mode.AEOLIAN))

        val taggedMajor = TaggedLevel(Game.CIRCLE,2,"Major - name to position", "", levelMajor, isFavourite = false, isCustom = false)
        val taggedMinor = TaggedLevel(Game.CIRCLE,3,"Minor - name to position", "", levelMinor, isFavourite = false, isCustom = false)
        val taggedDorian = TaggedLevel(Game.CIRCLE,4,"Dorian - name to position", "", levelDorian, isFavourite = false, isCustom = false)
        val taggedMixolydian = TaggedLevel(Game.CIRCLE,5,"Mixolydian - name to position", "", levelMixolydian, isFavourite = false, isCustom = false)
        val taggedLydian = TaggedLevel(Game.CIRCLE, 6, "Lydian - name to position", "", levelLydian, isFavourite = false, isCustom = false)

        val taggedMajorMinor = TaggedLevel(Game.CIRCLE, 7, "Major, minor", "name to position", levelMajorMinor, isFavourite = false, isCustom = false)
        return listOf(taggedMajor, taggedMinor, taggedDorian, taggedMixolydian, taggedMajorMinor)
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun prepareViewModel(level: Level, gameplay: GamePlayInstance, owner: ViewModelStoreOwner) {
        if(level !is CircleLevel) {
            throw IllegalArgumentException("level is not of type CircleLevel")
        }
        val gameLogic = GameLogicCircle(level)
        val viewModel = ViewModelProvider(owner)[CircleViewModel::class.java]
        viewModel.setLogic(gameLogic)
        viewModel.setBpm(gameplay.bpm.toLong())
        return
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