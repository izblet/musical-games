package com.example.musicalgames.game.games.flappy.creation

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.util.TypedValue
import android.widget.FrameLayout
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.flappy.FlappyGameController
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.flappy.FlappyViewModel
import com.example.musicalgames.games.flappy.FloppyGameView
import com.example.musicalgames.main_app.game_levels.TaggedLevel

class FlappyGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<TaggedLevel> {
        return FlappyLevels.baseLevels
    }

    override fun getPermissions(): Array<String> {
        return arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    override fun prepareViewModel(level: Level, gameplay: GamePlayInstance, owner: ViewModelStoreOwner) {
        val viewModel = ViewModelProvider(owner)[FlappyViewModel::class.java]
        viewModel.setLevel(level)
        return
    }

    override fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?): CustomGameCreator {
        return FlappyCustomCreator(context, createLevelAction, attrs)
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[FlappyViewModel::class.java]
        val gameView = FloppyGameView(context)
        gameContainer.addView(gameView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        val dp16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()
        val pauseButton = MaterialButton(context).apply {
            text = "Pause"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setPadding(dp16 * 2, dp16, dp16 * 2, dp16)
        }
        gameContainer.addView(pauseButton, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {
            it.gravity = Gravity.BOTTOM or Gravity.START
            it.setMargins(dp16, 0, 0, dp16)
        })

        val gameController = FlappyGameController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(context, gameListener)

        pauseButton.setOnClickListener {
            pauseButton.isEnabled = false
            gameController.pauseGame()
            Handler(Looper.getMainLooper()).postDelayed({
                pauseButton.isEnabled = true
            }, 3000)
        }

        return gameController
    }

}