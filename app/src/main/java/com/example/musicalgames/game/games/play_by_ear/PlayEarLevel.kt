package com.example.musicalgames.game.games.play_by_ear

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Note
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayEarLevel (
    override val id: Int,
    val minPitchDisplayed: Int, //TODO: this should be calculated later, remove this field and the next one
    val maxPitchDisplayed: Int,
    val root: ChromaticNote,
    val problemLen: Int,
    val maxSemitoneInterval: Int,
    val keyList: List<Int>,
    override val name: String,
    override val description: String

): Level(id, name, description)
