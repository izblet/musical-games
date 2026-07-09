package com.example.musicalgames.main_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.database.GameDatabase
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val levelDao = GameDatabase.getInstance(application).levelDao()

    //the "volatiles" are probably not necessary if nothing breaks, but the values are accessed from different threads
    @Volatile
    var game: Game? = null
    @Volatile
    var taggedLevel: TaggedLevel? = null   // null only before any level has ever been chosen; levelId null within it means temporary/unsaved
    @Volatile
    var gameplay: GamePlayInstance? = null

    val navigateToLevels = MutableSharedFlow<Unit>()
    val navigateToGamePlay = MutableSharedFlow<Unit>()
    val startGame = MutableSharedFlow<Unit>()

    fun chooseGame(game: Game) {
        this.game = game
        viewModelScope.launch { navigateToLevels.emit(Unit) }
    }

    fun chooseLevel(taggedLevel: TaggedLevel) {
        this.taggedLevel = taggedLevel
        viewModelScope.launch { navigateToGamePlay.emit(Unit) }
    }

    fun playLevel(gameplay: GamePlayInstance) {
        this.gameplay = gameplay
        viewModelScope.launch {  startGame.emit(Unit) }
    }

    //inserts the current working level as a new custom level, turning a temporary level into a saved one
    fun saveNewLevel(name: String, description: String, onSaved: () -> Unit) {
        val tagged = taggedLevel ?: return
        viewModelScope.launch {
            val toInsert = tagged.copy(name = name, description = description, isCustom = true)
            val id = levelDao.addLevel(toInsert, tagged.game)
            taggedLevel = toInsert.copy(levelId = id)
            onSaved()
        }
    }

    //writes the current working level/name/description back to the existing custom level's row
    fun updateLevel() {
        val tagged = taggedLevel ?: return
        val id = tagged.levelId ?: return
        viewModelScope.launch {
            levelDao.updateLevel(id, tagged.game, tagged.name, tagged.description, tagged.level)
        }
    }

    fun updateLevelInfo(name: String, description: String) {
        taggedLevel = taggedLevel?.copy(name = name, description = description)
        updateLevel()
    }

    fun updateLevelParams(newLevel: Level) {
        taggedLevel = taggedLevel?.copy(level = newLevel)
        updateLevel()
    }

    fun toggleFavourite(onDone: () -> Unit) {
        val tagged = taggedLevel ?: return
        val id = tagged.levelId ?: return
        viewModelScope.launch {
            val newVal = !tagged.isFavourite
            levelDao.changeFavourite(newVal, id)
            taggedLevel = tagged.copy(isFavourite = newVal)
            onDone()
        }
    }

    fun deleteLevel(onDone: () -> Unit) {
        val tagged = taggedLevel ?: return
        val id = tagged.levelId ?: return
        viewModelScope.launch {
            levelDao.deleteLevel(id)
            onDone()
        }
    }
}