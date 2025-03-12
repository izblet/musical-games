package com.example.musicalgames.games.flappy

import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName
import com.example.musicalgames.utils.Note
import com.example.musicalgames.utils.Scale

const val LEN_INF = -1
const val DELIMITER = ","
object FlappyLevels {
    val baseLevels: List<FlappyLevel> = generateMajorLevels()
    val minorLevels: List<FlappyLevel> = generateMinorLevels()
    private fun generateArcadeLevel(notes: List<Int>, root: Int, mode: String): FlappyLevel {
        val minPitch = notes[0]
        val maxPitch = notes[notes.size-1]

        val name = "${noteName(minPitch)} to ${noteName(maxPitch)}, root: ${noteLetter(root)} $mode"
        val description = "Arcade"
        return FlappyLevel(-1, minPitch, maxPitch, root, notes, name, description, LEN_INF)

    }

    private fun generateMajorLevels():List<FlappyLevel> {
        val levels = mutableListOf<FlappyLevel>()

        val rootNote = midi("C4")

        for(sizeList in listOf(listOf(3,4), listOf(5,6), listOf(7), listOf(8))) {
            //generate levels with notes above root
            for(size in sizeList) {
                val notes = MusicUtil.getWhiteKeysFrom(rootNote, size)
                levels.add(generateArcadeLevel(notes, rootNote, "major"))
            }
            //generate levels with notes below root
            for (size in sizeList) {
                val notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                levels.add(generateArcadeLevel(notes, rootNote, "major"))
            }

            //generate levels that concat notes below and notes above
            for(size in sizeList) {
                var notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                notes = notes.slice(0..(notes.size - 2)) + MusicUtil.getWhiteKeysFrom(rootNote, size)
                levels.add(generateArcadeLevel(notes, rootNote, "major"))
            }
        }

        return levels
    }
    private fun generateMinorLevels():List<FlappyLevel> {
        val levels = mutableListOf<FlappyLevel>()

        val rootNote = Note("A3")

        for(sizeList in listOf(listOf(3,4), listOf(5,6), listOf(7), listOf(8))) {
            //generate levels with notes above root
            for(size in sizeList) {
                val notes = MusicUtil.getScaleNotesFrom(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                levels.add(generateArcadeLevel(notes.map{it.midiCode}, rootNote.midiCode, "minor"))
            }
            //generate levels with notes below root
            for (size in sizeList) {
                val notes = MusicUtil.getScaleNotesTo(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                levels.add(generateArcadeLevel(notes.map{it.midiCode}, rootNote.midiCode, "minor"))
            }

            //generate levels that concat notes below and notes above
            for(size in sizeList) {
                var notes = MusicUtil.getScaleNotesTo(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                notes = notes.slice(0..(notes.size - 2)) + MusicUtil.getScaleNotesFrom(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                levels.add(generateArcadeLevel(notes.map{it.midiCode}, rootNote.midiCode, "minor"))
            }
        }

        return levels
    }

}
