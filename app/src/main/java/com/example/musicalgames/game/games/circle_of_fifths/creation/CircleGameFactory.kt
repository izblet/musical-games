package com.example.musicalgames.game.games.circle_of_fifths.creation

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
import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.game.games.circle_of_fifths.CircleView
import com.example.musicalgames.game.games.circle_of_fifths.CircleViewModel
import com.example.musicalgames.game.games.circle_of_fifths.GameLogicCircle
import com.example.musicalgames.game.games.circle_of_fifths.level_data.CircleLevels
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser

class CircleGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return CircleLevels.baseLevels
    }

    override fun getPermissions(gameplay: GamePlayInstance): Array<String> {
        return if (gameplay.inputMethod == InputMethod.EXTERNAL_INSTRUMENT) {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf()
        }
    }

    override fun prepareViewModel(level: Level, gameplay: GamePlayInstance, owner: ViewModelStoreOwner) {
        if(level !is CircleLevel) {
            throw IllegalArgumentException("level is not of type CircleLevel")
        }
        val gameLogic = GameLogicCircle(level)
        val viewModel = ViewModelProvider(owner)[CircleViewModel::class.java]
        viewModel.setLogic(gameLogic)
        viewModel.setBpm(gameplay.bpm.toLong())
        viewModel.gameplay = gameplay
        return
    }

    override fun getCustomCreator(
        context: Context,
        createLevelAction: (Level) -> Unit,
        attrs: AttributeSet?
    ): CustomGameCreator {
        return CircleCustomCreator(context, createLevelAction, attrs)
    }

    override fun getCustomCreatorFromLevel(context: Context, level: Level, attrs: AttributeSet?): CustomGameCreator {
        return CircleCustomCreator(context, level as CircleLevel, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        screenHighlighter: ScreenHighlighter,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[CircleViewModel::class.java]
        viewModel.setScreenHighlighter(screenHighlighter)
        val gameView = CircleView(context, viewModel, activity)
        gameContainer.addView(gameView)

        val tapSource = KeyPaletteNoteInputSource()
        gameView.setKeyboardListener(tapSource)

        val noteInputSource: ChromaticNoteInputSource = when (viewModel.gameplay.inputMethod) {
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
        viewModel.setNoteInput(noteInputSource)

        return viewModel
    }
}