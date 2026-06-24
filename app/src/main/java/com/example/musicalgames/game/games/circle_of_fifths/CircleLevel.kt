package com.example.musicalgames.game.games.circle_of_fifths

import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.music_model.Mode
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleLevel(
    val positionToName: Boolean,
    val modes: List<Mode>
) : Level() {
    override fun supportsMicrophoneInput(): Boolean = positionToName
}