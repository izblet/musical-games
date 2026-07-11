package com.example.musicalgames.game.games.flappy.creation

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.R
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.game.game_core.creation.GameFactory
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.games.flappy.FlappyController
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game.games.flappy.FlappyViewModel
import com.example.musicalgames.game.games.flappy.game_logic.Bird
import com.example.musicalgames.game.games.flappy.game_logic.GameLogic
import com.example.musicalgames.game.games.flappy.game_logic.MidiCoordinateController
import com.example.musicalgames.game.games.flappy.game_logic.PipeGenerator
import com.example.musicalgames.game.games.flappy.graphics.CircleBirdAppearance
import com.example.musicalgames.game.games.flappy.graphics.GameView
import com.example.musicalgames.game.games.flappy.graphics.flappy_renderer.StaffFlappyRenderer
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.flappy.LEN_INF
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel
import com.example.musicalgames.music_model.KeySignature
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.components.StaffPainter
import com.example.musicalgames.utils.components.key_establishment.RootKeyEstablishmentPlayer
import com.example.musicalgames.utils.geometry.Circle
import com.example.musicalgames.utils.geometry.Point
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.question_generation.ListNoteGenerator
import com.example.musicalgames.utils.wrappers.BitmapUtil
import com.example.musicalgames.utils.wrappers.sound_recording.SwiftF0PitchRecogniser
import kotlinx.coroutines.launch

class FlappyGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return FlappyLevels.baseLevels
    }

    override fun getPermissions(gameplay: GamePlayInstance): Array<String> {
        return arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    override fun prepareViewModel(level: Level, gameplay: GamePlayInstance, owner: ViewModelStoreOwner) {
        val viewModel = ViewModelProvider(owner)[FlappyViewModel::class.java]
        val flappyLevel = level as FlappyLevel
        viewModel.setLevel(flappyLevel)
        viewModel.setKeyEstablishmentPlayer(RootKeyEstablishmentPlayer(flappyLevel.root))
    }

    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return FlappyCustomCreator(context, createLevelAction, attrs)
    }

    override fun getCustomCreatorFromLevel(context: Context, level: Level, attrs: AttributeSet?): CustomGameCreator {
        return FlappyCustomCreator(context, level as FlappyLevel, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        screenHighlighter: ScreenHighlighter,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[FlappyViewModel::class.java]
        viewModel.setScreenHighlighter(screenHighlighter)
        viewModel.clear() //viewmodel might be reused between games, makes sure no leftover view
        //TODO: the need for the line above seems ugly, consider scoping the viewmodel to fragment
        val level = viewModel.level

        //the game-space rectangle's height directly represents the level's midi pitch range
        //(plus half a semitone of padding on each side); its width is derived from the
        //screen's aspect ratio so that one game-space unit looks the same size on both axes
        val top = level.maxPitch + 1.5
        val bottom = level.minPitch - 1.5
        val gameHeight = top - bottom

        val displayMetrics = context.resources.displayMetrics
        val screenAspectRatio = displayMetrics.widthPixels.toDouble() / displayMetrics.heightPixels.toDouble()
        val gameWidth = gameHeight * screenAspectRatio

        val gameRect = Rect(left = 0.0, right = gameWidth, top = top, bottom = bottom)

        //pipe width/distance and speed keep the same proportions to the game-space width
        //that they were originally tuned for (with a width of 18.0)
        //TODO: pipeWidth should be calculated from physical dimensions (cm and such, knowing screen size)
        val pipeWidth = gameWidth / 25.0
        val pipeDistance = gameWidth / 3.0
        //speed is in game-space units per second (GameLogic.movePipes scales by deltaTime).
        val speed = gameWidth / 6.0

        //the bird's diameter represents the targeting precision: half a semitone, same as
        //the pitch-detection display precision in the previous version of this game
        // TODO: hardcoded for now, should probably come from a difficulty/precision setting
        val birdRadius = 0.25

        val micSettings = MicrophoneSettingsRepository(context).get()
        val pitchRecogniser = SwiftF0PitchRecogniser(
            context, "C2", "C6",
            micSettings.energyThreshold, micSettings.minConfidence
        )
        val midiCoordinateController = MidiCoordinateController(
            pitchRecogniser,
            minCoordinate = gameRect.bottom,
            maxCoordinate = gameRect.top
        )

        //distance between keys in midi coordinates
        val keyPadding =.1

        val pipeGenerator = PipeGenerator(
            width = pipeWidth,
            minHoleMidi = level.keyList.min(),
            maxHoleMidi = level.keyList.max(),
            noteGenerator = ListNoteGenerator(level.keyList),
            holePadding = keyPadding
        )

        val bird = Bird(
            shape = Circle(centerPoint = Point(gameWidth / 6.0, (gameRect.top + gameRect.bottom) / 2), radius = birdRadius),
            moveSpeedDiv = 10
        )

        val gameLogic = GameLogic(
            bird = bird,
            gameRectMidiCoordinates = gameRect,
            pipeDistance = pipeDistance,
            speed = speed,
            pipeGenerator = pipeGenerator,
            midiCoordinateController = midiCoordinateController,
            endAfter = level.endAfter.takeIf { it != LEN_INF }
        )
        viewModel.setLogic(gameLogic)

        val foregroundColor = TypedValue().let { tv ->
            context.theme.resolveAttribute(android.R.attr.colorForeground, tv, true)
            tv.data
        }

        val sharpBitmap = BitmapUtil.decodeSampledBitmap(context.resources, R.drawable.sharp, 256)
        val flatBitmap = BitmapUtil.decodeSampledBitmap(context.resources, R.drawable.flat, 256)

        val trebleBitmap = BitmapUtil.decodeSampledBitmap(context.resources, R.drawable.treble_clef, 256)
        val treblePainter = StaffPainter(trebleBitmap, sharpBitmap, flatBitmap, foregroundColor).apply { setTreble(true) }

        val bassBitmap = BitmapUtil.decodeSampledBitmap(context.resources, R.drawable.bass_clef, 256)
        val bassPainter = StaffPainter(bassBitmap, sharpBitmap, flatBitmap, foregroundColor).apply { setTreble(false) }

        val keySignature = KeySignature.forKey(Note(level.root).noteChromatic, level.mode)
        treblePainter.setKeySignature(keySignature)
        bassPainter.setKeySignature(keySignature)

        val renderer = StaffFlappyRenderer(
            treblePainter = treblePainter,
            bassPainter = bassPainter,
            birdAppearance = CircleBirdAppearance(),
            laneColor = context.getColor(R.color.blue),
            keyPadding = keyPadding
        )

        val gameView = GameView(context, viewModel, renderer, gameRect, activity)
        gameContainer.addView(gameView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        val messageTextView = TextView(context).apply {
            textSize = 20f
            gravity = Gravity.CENTER_HORIZONTAL
            setTextColor(foregroundColor)
        }
        gameContainer.addView(messageTextView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.TOP })

        activity.lifecycleScope.launch {
            viewModel.keyEstablishmentMessage.collect { messageTextView.text = it }
        }

//        val dp16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()
//        val pauseButton = MaterialButton(context).apply {
//            text = "Pause"
//            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
//            setPadding(dp16 * 2, dp16, dp16 * 2, dp16)
//        }
//        gameContainer.addView(pauseButton, FrameLayout.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        ).also {
//            it.gravity = Gravity.BOTTOM or Gravity.START
//            it.setMargins(dp16, 0, 0, dp16)
//        })

        val gameController = FlappyController(viewModel, pitchRecogniser)
        gameController.initGame(context, gameListener)

//        pauseButton.setOnClickListener {
//            pauseButton.isEnabled = false
//            gameController.pauseGame()
//            Handler(Looper.getMainLooper()).postDelayed({
//                pauseButton.isEnabled = true
//            }, 3000)
//        }

        return gameController
    }

}
