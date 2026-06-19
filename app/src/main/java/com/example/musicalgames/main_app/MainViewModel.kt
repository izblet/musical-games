package com.example.musicalgames.main_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.database.GameDatabase
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val levelDao = GameDatabase.getInstance(application).levelDao()

    //the "volatiles" are probably not necessary if nothing breaks, but the values are accessed from different threads
    @Volatile
    var game: Game? = null
    @Volatile
    var level: Level? = null
    @Volatile
    var levelName: String? = null
    @Volatile
    var levelDescription: String? = null
    @Volatile
    var levelId: Int? = null
    @Volatile
    var isCustom: Boolean? = null
    @Volatile
    var gameplay: GamePlayInstance? = null

    val navigateToLevels = MutableSharedFlow<Unit>()
    val navigateToGamePlay = MutableSharedFlow<Unit>()
    val startGame = MutableSharedFlow<Unit>()

    fun chooseGame(game: Game) {
        this.game = game
        viewModelScope.launch { navigateToLevels.emit(Unit) }
    }

    fun chooseLevel(level: Level, name: String? = null, description: String? = null, levelId: Int? = null, isCustom: Boolean? = null) {
        this.level = level
        this.levelName = name
        this.levelDescription = description
        this.levelId = levelId
        this.isCustom = isCustom
        viewModelScope.launch { navigateToGamePlay.emit(Unit) }
    }

    fun playLevel(gameplay: GamePlayInstance) {
        this.gameplay = gameplay
        viewModelScope.launch {  startGame.emit(Unit) }
    }

    //inserts the current working level as a new custom level, turning a temporary level into a saved one
    fun saveNewLevel(name: String, description: String, onSaved: () -> Unit) {
        val game = this.game ?: return
        val level = this.level ?: return
        viewModelScope.launch {
            val taggedLevel = TaggedLevel(game, 0, name, description, level, isFavourite = false, isCustom = true)
            levelId = levelDao.addLevel(taggedLevel, game)
            levelName = name
            levelDescription = description
            isCustom = true
            onSaved()
        }
    }

    //writes the current working level/name/description back to the existing custom level's row
    fun updateLevel() {
        val game = this.game ?: return
        val id = this.levelId ?: return
        val level = this.level ?: return
        val name = levelName ?: ""
        val description = levelDescription ?: ""
        viewModelScope.launch {
            levelDao.updateLevel(id, game, name, description, level)
        }
    }
}