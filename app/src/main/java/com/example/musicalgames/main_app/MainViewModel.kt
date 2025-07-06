package com.example.musicalgames.main_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    //the "volatiles" are probably not necessary if nothing breaks, but the values are accessed from different threads
    @Volatile
    var game: Game? = null
    @Volatile
    var level: Level? = null
    @Volatile
    var gameplay: GamePlayInstance? = null

    val navigateToLevels = MutableSharedFlow<Unit>()
    val navigateToGamePlay = MutableSharedFlow<Unit>()
    val startGame = MutableSharedFlow<Unit>()

    fun chooseGame(game: Game) {
        this.game = game
        viewModelScope.launch { navigateToLevels.emit(Unit) }
    }

    fun chooseLevel(level: Level) {
        this.level = level
        viewModelScope.launch { navigateToGamePlay.emit(Unit) }
    }

    fun playLevel(gameplay: GamePlayInstance) {
        this.gameplay = gameplay
        viewModelScope.launch {  startGame.emit(Unit) }
    }
}