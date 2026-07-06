package com.example.musicalgames.game.games.mental_intervals.creation

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.game.game_core.creation.GameFactory
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.InputMethod
import com.example.musicalgames.game.game_core.input.ChromaticNoteInputSource
import com.example.musicalgames.game.game_core.input.KeyPaletteNoteInputSource
import com.example.musicalgames.game.game_core.input.MicrophoneChromaticNoteInput
import com.example.musicalgames.game.game_core.input.MicrophoneNoteInput
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.mental_intervals.MentalController
import com.example.musicalgames.games.mental_intervals.MentalLevels
import com.example.musicalgames.games.mental_intervals.MentalView
import com.example.musicalgames.games.mental_intervals.MentalViewModel
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.wrappers.sound_recording.SwiftF0PitchRecogniser

class MentalGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return MentalLevels.intervalNoteLevels
    }

    override fun getPermissions(gameplay: GamePlayInstance): Array<String> {
        return if (gameplay.inputMethod == InputMethod.EXTERNAL_INSTRUMENT) {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf()
        }
    }

    override fun prepareViewModel(level: Level, gameplay: GamePlayInstance, owner: ViewModelStoreOwner) {
        val viewModel: MentalViewModel = ViewModelProvider(owner)[MentalViewModel::class.java]
        viewModel.setLevel(level, gameplay)
        return
    }


    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return MentalCustomCreator(context, createLevelAction, attrs)
    }

    override fun getCustomCreatorFromLevel(context: Context, level: Level, attrs: AttributeSet?): CustomGameCreator {
        return MentalCustomCreator(context, level as MentalLevel, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        screenHighlighter: ScreenHighlighter,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[MentalViewModel::class.java]
        viewModel.setScreenHighlighter(screenHighlighter)
        val gameView = MentalView(context)
        gameView.setViewModel(viewModel)
        gameContainer.addView(gameView)

        val tapSource = KeyPaletteNoteInputSource()
        gameView.setKeyboardListener(tapSource)

        val noteInputSource: ChromaticNoteInputSource = when (viewModel.gameplay.inputMethod) {
            InputMethod.ONSCREEN -> tapSource
            InputMethod.EXTERNAL_INSTRUMENT -> {
                val micSettings = MicrophoneSettingsRepository(context).get()
                val pitchRecogniser = SwiftF0PitchRecogniser(
                    context, "C2", "C6",
                    micSettings.energyThreshold, micSettings.minConfidence
                )
                MicrophoneChromaticNoteInput(MicrophoneNoteInput.withSettings(pitchRecogniser, micSettings))
            }
        }
        viewModel.setNoteInput(noteInputSource)

        val gameController = MentalController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(context, gameListener)
        return gameController
    }
}