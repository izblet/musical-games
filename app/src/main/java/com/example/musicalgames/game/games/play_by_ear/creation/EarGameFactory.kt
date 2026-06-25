package com.example.musicalgames.game.games.play_by_ear.creation

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
import com.example.musicalgames.game.game_core.input.MicrophoneNoteInput
import com.example.musicalgames.game.game_core.input.NoteInputSource
import com.example.musicalgames.game.game_core.input.OnscreenNoteInputSource
import com.example.musicalgames.game.games.play_by_ear.EarController
import com.example.musicalgames.game.games.play_by_ear.EarView
import com.example.musicalgames.game.games.play_by_ear.EarViewModel
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.play_by_ear.EarPlayLevels
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser

class EarGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return EarPlayLevels.baseLevels
    }

    override fun getPermissions(gameplay: GamePlayInstance): Array<String> {
        return if (gameplay.inputMethod == InputMethod.EXTERNAL_INSTRUMENT) {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf()
        }
    }

    override fun prepareViewModel(level: Level, gameplay: GamePlayInstance, owner: ViewModelStoreOwner) {
        val viewModel = ViewModelProvider(owner)[EarViewModel::class.java]
        viewModel.setLevel(level, gameplay)
        return
    }


    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return EarCreatorView(context, createLevelAction, attrs)
    }

    override fun getCustomCreatorFromLevel(context: Context, level: Level, attrs: AttributeSet?): CustomGameCreator {
        return EarCreatorView(context, level as PlayEarLevel, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        screenHighlighter: ScreenHighlighter,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[EarViewModel::class.java]
        viewModel.setScreenHighlighter(screenHighlighter)
        val level = viewModel.level!!
        viewModel.setNoteDurationMs(NOTE_DURATION_MS) //TODO: placeholder value, revisit once the new recordings are tuned
        val onscreenSource = OnscreenNoteInputSource()
        val gameView = EarView(context, null, viewModel, activity, onscreenSource)
        gameContainer.addView(gameView)

        val noteInputSource: NoteInputSource = when (viewModel.gameplay.inputMethod) {
            InputMethod.ONSCREEN -> onscreenSource
            InputMethod.EXTERNAL_INSTRUMENT -> {
                val minNote = NoteSpelling.spell(Note(level.minPitchDisplayed), SpellingPreference.SHARPS)
                val maxNote = NoteSpelling.spell(Note(level.maxPitchDisplayed), SpellingPreference.SHARPS)
                val micSettings = MicrophoneSettingsRepository(context).get()
                val pitchRecogniser = PitchRecogniser(
                    context, minNote, maxNote,
                    micSettings.energyThreshold, micSettings.maxUncertainty
                )
                MicrophoneNoteInput.withSettings(pitchRecogniser, micSettings)
            }
        }

        val gameController = EarController(viewModel, noteInputSource)
        gameController.initGame(context, gameListener)

        return gameController
    }

    companion object {
        private const val NOTE_DURATION_MS = 800L
    }
}