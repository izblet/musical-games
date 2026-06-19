package com.example.musicalgames.game.games.mental_intervals

import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.mental_intervals.Type
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Interval
import kotlinx.parcelize.Parcelize

@Parcelize
data class MentalLevel (
    val startingNotes: List<ChromaticNote>,
    val intervals: List<Interval>,
    val mode: Type,
): Level()