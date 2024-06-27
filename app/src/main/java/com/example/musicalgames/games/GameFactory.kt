package com.example.musicalgames.games

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level

interface GameFactory {
    fun getPackages() : List<GamePackage>
    fun getLevels(pack: GamePackage) : List<Level>
    fun getPermissions() : Array<String>
    fun getViewModelType() : Class<out ViewModel>
    //TODO: the following is only temporary - there will be no 'intent maker', we will pass level index as argument or sth
    fun getIntentMaker() : GameIntentMaker
    fun createGame( context: Context, activity: FragmentActivity, gameContainer: ViewGroup, gameListener: GameListener) : GameController
}