package com.example.musicalgames.game.game_core

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.GamePackage

interface GameFactory {
    suspend fun getLevels(pack: GamePackage, context: Context) : List<Level>
    fun getPermissions() : Array<String>
    fun getViewModelType() : Class<out ViewModel>
    fun getCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator
    fun createGame( context: Context, activity: FragmentActivity, gameContainer: ViewGroup, gameListener: GameListener) : GameController
}