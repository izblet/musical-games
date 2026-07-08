package com.example.musicalgames.game.games.chords.level_data

import com.example.musicalgames.game.games.chords.LevelChords
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel
import com.example.musicalgames.music_model.Chord
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.DiatonicNote

object ChordsLevels {
    private val diatonicRoots = DiatonicNote.entries.map { it.chromaticNote }.toSet()
    private val allRoots = ChromaticNote.entries.toSet()
    private val allQualities = Chord.Companion.Quality.entries.toSet()
    private val allExtensions = Chord.Companion.Extension.entries.toSet()
    private val noExtension = setOf<Chord.Companion.Extension?>(null)

    val baseLevels: List<TaggedLevel> = listOf(
        level("diatonic roots", "", diatonicRoots, allQualities, allExtensions),
        level("all possible", "", allRoots, allQualities, allExtensions),
        level("major chords", "", allRoots, setOf(Chord.Companion.Quality.MAJOR), noExtension),
        level("minor chords", "", allRoots, setOf(Chord.Companion.Quality.MINOR), noExtension),
        level("minor and major chords", "", allRoots, setOf(Chord.Companion.Quality.MINOR, Chord.Companion.Quality.MAJOR), noExtension),
    )

    private fun level(name: String, description: String, roots: Set<ChromaticNote>, qualities: Set<Chord.Companion.Quality>, extensions: Set<Chord.Companion.Extension?>): TaggedLevel =
        TaggedLevel(Game.CHORDS, 0, name, description, LevelChords(roots, qualities, extensions), isFavourite = false, isCustom = false)
}
