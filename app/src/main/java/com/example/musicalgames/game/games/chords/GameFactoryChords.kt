package com.example.musicalgames.game.games.chords

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game.game_core.creation.GameFactory
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.InputMethod
import com.example.musicalgames.game.game_core.input.ChromaticNoteInputSource
import com.example.musicalgames.game.game_core.input.KeyPaletteNoteInputSource
import com.example.musicalgames.game.game_core.input.MicrophoneChromaticNoteInput
import com.example.musicalgames.game.game_core.input.MicrophoneNoteInput
import com.example.musicalgames.game.games.chords.level_data.ChordsLevels
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser

class GameFactoryChords: GameFactory {
    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return ChordsLevels.baseLevels
    }

    override fun getPermissions(gameplay: GamePlayInstance): Array<String> {
        return if (gameplay.inputMethod == InputMethod.EXTERNAL_INSTRUMENT) {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf()
        }
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
        viewModel.gameplay = gameplay
    }

    override fun getCustomCreator(
        context: Context,
        createLevelAction: (Level) -> Unit,
        attrs: AttributeSet?
    ): CustomGameCreator {
        return CustomCreatorChords(context, createLevelAction, attrs)
    }

    override fun getCustomCreatorFromLevel(context: Context, level: Level, attrs: AttributeSet?): CustomGameCreator {
        return CustomCreatorChords(context, level as LevelChords, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        screenHighlighter: ScreenHighlighter,
        gameListener: GameListener
    ): GameController {
        val viewmodel = ViewModelProvider(activity)[ViewModelChords::class.java]
        viewmodel.setScreenHighlighter(screenHighlighter)
        val tapSource = KeyPaletteNoteInputSource()
        val gameView = ViewChords(context, viewmodel, activity, tapSource)
        gameContainer.addView(gameView)

        val noteInputSource: ChromaticNoteInputSource = when (viewmodel.gameplay.inputMethod) {
            InputMethod.ONSCREEN -> tapSource
            InputMethod.EXTERNAL_INSTRUMENT -> {
                val micSettings = MicrophoneSettingsRepository(context).get()
                val pitchRecogniser = PitchRecogniser(
                    context, "C2", "C6",
                    micSettings.energyThreshold, micSettings.maxUncertainty
                )
                MicrophoneChromaticNoteInput(MicrophoneNoteInput.withSettings(pitchRecogniser, micSettings))
            }
        }
        viewmodel.setNoteInput(noteInputSource)

        return viewmodel
    }
}