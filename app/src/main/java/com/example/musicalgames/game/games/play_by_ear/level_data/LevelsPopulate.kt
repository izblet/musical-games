package com.example.musicalgames.games.play_by_ear

import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.MusicUtil.getWhiteKeysFrom
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.Scale
import com.example.musicalgames.music_model.display.SpellingPreference

object EarPlayLevels {
    val baseLevels: List<TaggedLevel> = generateLevels()
    private fun generateLevel(scale: Scale, rootNote: ChromaticNote, notes: List<Int>, notesNum: Int, interval: Int) : TaggedLevel {
        val id =0
        val span = notes[notes.size-1]-notes[0]
        //if the span is at least an octave, we just show the note
        //if the notes end on the root, we show the octave below
        //otherwise we show the octave above
        val firstNote = Note(notes[0]).noteChromatic
        val lastNote = Note(notes[notes.size-1]).noteChromatic
        val firstRootBelow =
            if(firstNote.ordinal >=rootNote.ordinal)
                Note(rootNote, Note(notes[0]).octave).midiCode
            else
                Note(rootNote, Note(notes[0]).octave-1).midiCode

        val firstRootAbove =
            if(lastNote.ordinal <= rootNote.ordinal)
                Note(rootNote, Note(notes[notes.size-1]).octave).midiCode
            else
                Note(rootNote, Note(notes[notes.size-1]).octave+1).midiCode

        val belowDistance = notes[0] - firstRootBelow
        val aboveDistance = firstRootAbove - notes[notes.size - 1]


        val min =
            if(span<12 && (belowDistance<aboveDistance || lastNote == rootNote))
                firstRootBelow
            else notes[0]

        val max =
            if(span<12 && (belowDistance>=aboveDistance || firstNote == rootNote))
                firstRootAbove
            else notes[notes.size-1]
        val name = "$rootNote $scale, ${NoteSpelling.spell(Note(notes[0]), SpellingPreference.SHARPS)} to ${NoteSpelling.spell(Note(notes[notes.size-1]), SpellingPreference.SHARPS)}"
        val description = "$notesNum notes, max interval $interval semitones"
        val level = PlayEarLevel(
            min,
            max,
            rootNote,
            notesNum,
            interval,
            notes.toList(),
        )
        return TaggedLevel(Game.PLAY_BY_EAR, id,name=name, description=description, level, isFavourite = false, isCustom = false)
    }
    private fun generateLevels(): List<TaggedLevel> {
        val levels = mutableListOf<TaggedLevel>()
        val rootNote = Note(ChromaticNote.C, 4).midiCode
        for(maxInterval in listOf(4,5,6,7,8)) {
            for (notesNum in listOf(3, 5, 7)) {
                for (sizeList in listOf(listOf(3, 4), listOf(5, 6), listOf(7), listOf(8))) {
                    //generate levels with notes above root
                    for (size in sizeList) {
                        val notes = getWhiteKeysFrom(rootNote, size)
                        levels.add(generateLevel(Scale.MAJOR, ChromaticNote.C, notes, notesNum, maxInterval))
                    }
                    //generate levels with notes below root
                    for (size in sizeList) {
                        val notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                        levels.add(generateLevel(Scale.MAJOR, ChromaticNote.C, notes, notesNum, maxInterval))
                    }

                    //generate levels that concat notes below and notes above
                    for (size in sizeList) {
                        var notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                        notes = notes.slice(0..(notes.size - 2)) + getWhiteKeysFrom(
                            rootNote,
                            size
                        )
                        levels.add(generateLevel(Scale.MAJOR, ChromaticNote.C, notes, notesNum, maxInterval))
                    }
                }
            }
        }

        return levels
    }

}



