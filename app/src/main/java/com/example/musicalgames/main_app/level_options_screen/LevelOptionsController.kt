package com.example.musicalgames.main_app.level_options_screen

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.main_app.MainViewModel
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel

class LevelOptionsController(private val mainViewModel: MainViewModel) {

    enum class EditSection { NONE, INFO, PARAMS }

    val game: Game? get() = mainViewModel.game
    val taggedLevel: TaggedLevel? get() = mainViewModel.taggedLevel
    val level: Level? get() = taggedLevel?.level
    val levelName: String get() = taggedLevel?.name ?: ""
    val levelDescription: String get() = taggedLevel?.description ?: ""
    val isCustom: Boolean? get() = taggedLevel?.isCustom
    val temporaryTitle: Boolean get() = taggedLevel?.levelId == null

    //info editing needs an already-persisted custom level; params stay editable for a temporary (not-yet-saved) level too
    val infoEditable: Boolean get() = taggedLevel?.isInfoEditable() ?: false
    val parametersEditable: Boolean get() = taggedLevel?.isEditable() ?: false

    var activeEditSection: EditSection = EditSection.NONE
        private set

    //the last-known-good level for the params section - "discard" reverts to this
    private var workingLevel: Level? = mainViewModel.taggedLevel?.level

    fun beginEditingParams() {
        activeEditSection = EditSection.PARAMS
    }

    fun beginEditingInfo() {
        activeEditSection = EditSection.INFO
    }

    fun saveParamsEdit(newLevel: Level) {
        workingLevel = newLevel
        mainViewModel.updateLevelParams(newLevel)
        activeEditSection = EditSection.NONE
    }

    fun discardParamsEdit(): Level? {
        activeEditSection = EditSection.NONE
        return workingLevel
    }

    fun saveInfoEdit(name: String, description: String) {
        mainViewModel.updateLevelInfo(name, description)
        activeEditSection = EditSection.NONE
    }

    fun cancelInfoEdit() {
        activeEditSection = EditSection.NONE
    }

    fun saveNewLevel(name: String, description: String, onSaved: () -> Unit) {
        mainViewModel.saveNewLevel(name, description, onSaved)
    }

    fun requiredPermissions(gameplay: GamePlayInstance): Array<String> =
        game?.let { GameMap.createFactory(it).getPermissions(gameplay) } ?: emptyArray()

    fun hasRequiredPermissions(context: Context, gameplay: GamePlayInstance): Boolean =
        requiredPermissions(gameplay).all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun startGame(gameplay: GamePlayInstance) {
        mainViewModel.playLevel(gameplay)
    }


}