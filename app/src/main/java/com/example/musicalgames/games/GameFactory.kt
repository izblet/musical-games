package com.example.musicalgames.games

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level

interface GameFactory {
    suspend fun getLevels(pack: GamePackage, context: Context) : List<Level>
    fun getPermissions() : Array<String>
    fun getViewModelType() : Class<out ViewModel>
    //TODO: the following is only temporary - there will be no 'intent maker', we will pass level index as argument or sth
    fun getIntentMaker() : GameIntentMaker
    fun getCustomCreator(context: Context, attrs: AttributeSet) : CustomGameCreator
    fun createGame( context: Context, activity: FragmentActivity, gameContainer: ViewGroup, gameListener: GameListener) : GameController
}