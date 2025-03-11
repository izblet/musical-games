package com.example.musicalgames.game.games.mental_intervals

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.mental_intervals.Type
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import kotlinx.parcelize.Parcelize

@Parcelize
data class MentalLevel (
    override val id: Int,
    val startingNotes: List<ChromaticNote>,
    val intervals: List<Interval>,
    val mode: Type,
    override val name: String,
    override val description: String,
): Level(id, name, description)