package com.example.musicalgames.main_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LevelOptionsViewModel(private val mainViewModel: MainViewModel) : ViewModel() {

    enum class EditSection { NONE, INFO, PARAMS }

    val game: Game? get() = mainViewModel.game
    val level: Level? get() = mainViewModel.level
    val levelName: String? get() = mainViewModel.levelName
    val levelDescription: String? get() = mainViewModel.levelDescription
    val levelId: Int? get() = mainViewModel.levelId
    val isCustom: Boolean? get() = mainViewModel.isCustom

    //temporary -> should be made into variables with simple access, here we use it so that we can test without plugging in
    data class UIState (
        val infoEditable : Boolean,
        val parametersEditable : Boolean,
        val temporaryTitle : Boolean,
        val activeEditSection: EditSection
    )

    private val _activeEditSection = MutableStateFlow(EditSection.NONE)
    val activeEditSection: StateFlow<EditSection> = _activeEditSection.asStateFlow()

    private val _invalidBpmError = MutableSharedFlow<Unit>()
    val invalidBpmError: SharedFlow<Unit> = _invalidBpmError

    private var workingLevel: Level? = mainViewModel.level

    fun beginEditingParams() {
        _activeEditSection.value = EditSection.PARAMS
    }

    fun beginEditingInfo() {
        _activeEditSection.value = EditSection.INFO
    }

    fun saveParamsEdit(newLevel: Level) {
        workingLevel = newLevel
        mainViewModel.level = newLevel
        if (mainViewModel.levelId != null) {
            mainViewModel.updateLevel()
        }
        _activeEditSection.value = EditSection.NONE
    }

    fun discardParamsEdit(): Level? {
        _activeEditSection.value = EditSection.NONE
        return workingLevel
    }

    fun saveInfoEdit(name: String, description: String) {
        mainViewModel.levelName = name
        mainViewModel.levelDescription = description
        if (mainViewModel.levelId != null) {
            mainViewModel.updateLevel()
        }
        _activeEditSection.value = EditSection.NONE
    }

    fun cancelInfoEdit() {
        _activeEditSection.value = EditSection.NONE
    }

    fun saveNewLevel(name: String, description: String, onSaved: () -> Unit) {
        mainViewModel.saveNewLevel(name, description, onSaved)
    }

    fun startGame(bpmInput: String?) {
        val bpm = bpmInput?.toIntOrNull()
        if (bpm == null) {
            mainViewModel.playLevel(GamePlayInstance())
            return
        }
        try {
            mainViewModel.playLevel(GamePlayInstance(bpm = bpm))
        } catch (e: Exception) {
            viewModelScope.launch { _invalidBpmError.emit(Unit) }
        }
    }
}
