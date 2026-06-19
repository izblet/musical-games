package com.example.musicalgames.game.games.chords

import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.music_model.Chord
import com.example.musicalgames.music_model.ChromaticNote
import kotlinx.parcelize.Parcelize

@Parcelize
data class LevelChords(val startingNotes: Set<ChromaticNote>,
                       val qualities: Set<Chord.Companion.Quality>,
                       val extensions: Set<Chord.Companion.Extension?>) : Level()
