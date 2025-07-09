package com.example.musicalgames.game.games.chords

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.utils.Chord
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote

class GameFactoryChords: GameFactory {
    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        val levelAll = LevelChords(ChromaticNote.entries.toSet(), Chord.Companion.Quality.entries.toSet(), Chord.Companion.Extension.entries.toSet())
        val taggedLevelAll = TaggedLevel(Game.CHORDS, 1, "all possible", "", levelAll, isFavourite = false, isCustom = false)

        val diatonicNotes = DiatonicNote.entries.toList().map{ diatonicNote -> diatonicNote.chromaticNote }
        val levelDiatonic = LevelChords(diatonicNotes.toSet(), Chord.Companion.Quality.entries.toSet(), Chord.Companion.Extension.entries.toSet())
        val taggedLevelDiatonic = TaggedLevel(Game.CHORDS, 2, "diatonic roots", "", levelDiatonic, isFavourite = false, isCustom = false)
       return listOf(taggedLevelDiatonic, taggedLevelAll)
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun prepareViewModel(
        level: Level,
        gameplay: GamePlayInstance,
        owner: ViewModelStoreOwner
    ) {
        if(level !is LevelChords)
            throw IllegalArgumentException("Wrong level type")

        val viewModel = ViewModelProvider(owner)[ViewModelChords::class.java]
        viewModel.setLogic(GameLogicChords(level.startingNotes,level.extensions,level.qualities))
        viewModel.setBpm(gameplay.bpm.toLong())
    }

    override fun getCustomCreator(
        context: Context,
        createLevelAction: (Level) -> Unit,
        attrs: AttributeSet?
    ): CustomGameCreator {
        return CustomCreatorChords(context, createLevelAction, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewmodel = ViewModelProvider(activity)[ViewModelChords::class.java]
        val gameView = ViewChords(context, viewmodel,activity)
        gameContainer.addView(gameView)
        return viewmodel
    }
}