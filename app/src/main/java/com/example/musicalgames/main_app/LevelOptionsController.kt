package com.example.musicalgames.main_app

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

class LevelOptionsController(private val mainViewModel: MainViewModel) {

    enum class EditSection { NONE, INFO, PARAMS }

    val game: Game? get() = mainViewModel.game
    val level: Level? get() = mainViewModel.level
    val levelName: String? get() = mainViewModel.levelName
    val levelDescription: String? get() = mainViewModel.levelDescription
    val levelId: Int? get() = mainViewModel.levelId
    val isCustom: Boolean? get() = mainViewModel.isCustom

    //predefined levels (isCustom == false) can't be edited; temporary levels (isCustom == null) can
    val infoEditable: Boolean get() = isCustom == true
    val parametersEditable: Boolean get() = isCustom != false
    val temporaryTitle: Boolean get() = isCustom == null

    var activeEditSection: EditSection = EditSection.NONE
        private set

    //the last-known-good level for the params section - "discard" reverts to this
    private var workingLevel: Level? = mainViewModel.level

    fun beginEditingParams() {
        activeEditSection = EditSection.PARAMS
    }

    fun beginEditingInfo() {
        activeEditSection = EditSection.INFO
    }

    fun saveParamsEdit(newLevel: Level) {
        workingLevel = newLevel
        mainViewModel.level = newLevel
        if (mainViewModel.levelId != null) {
            mainViewModel.updateLevel()
        }
        activeEditSection = EditSection.NONE
    }

    fun discardParamsEdit(): Level? {
        activeEditSection = EditSection.NONE
        return workingLevel
    }

    fun saveInfoEdit(name: String, description: String) {
        mainViewModel.levelName = name
        mainViewModel.levelDescription = description
        if (mainViewModel.levelId != null) {
            mainViewModel.updateLevel()
        }
        activeEditSection = EditSection.NONE
    }

    fun cancelInfoEdit() {
        activeEditSection = EditSection.NONE
    }

    fun saveNewLevel(name: String, description: String, onSaved: () -> Unit) {
        mainViewModel.saveNewLevel(name, description, onSaved)
    }

    val requiredPermissions: Array<String>
        get() = game?.let { GameMap.createFactory(it).getPermissions() } ?: emptyArray()

    fun hasRequiredPermissions(context: Context): Boolean =
        requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    //returns false if the bpm couldn't be used to start the game (caller should show an error)
    fun startGame(bpmInput: String?): Boolean {
        val bpm = bpmInput?.toIntOrNull()
        if (bpm == null) {
            mainViewModel.playLevel(GamePlayInstance())
            return true
        }
        return try {
            mainViewModel.playLevel(GamePlayInstance(bpm = bpm))
            true
        } catch (e: Exception) {
            false
        }
    }


}
