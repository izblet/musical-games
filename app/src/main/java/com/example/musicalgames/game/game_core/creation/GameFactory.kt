package com.example.musicalgames.game.game_core.creation

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.game_levels.TaggedLevel

interface GameFactory {

    suspend fun getLevels(pack: GamePackage, context: Context) : List<TaggedLevel>
    fun getPermissions(gameplay: GamePlayInstance) : Array<String>
    fun getStartDelayMs() : Long { return 0 }
    fun prepareViewModel(
        level: Level,
        gameplay: GamePlayInstance,
        owner: ViewModelStoreOwner
    )

    fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator
    fun getCustomCreatorFromLevel(context: Context, level: Level, attrs: AttributeSet?) : CustomGameCreator
    fun createGame(context: Context, activity: FragmentActivity, gameContainer: ViewGroup, screenHighlighter: ScreenHighlighter, gameListener: GameListener) : GameController
}