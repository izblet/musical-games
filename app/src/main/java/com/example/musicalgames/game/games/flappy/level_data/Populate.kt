package com.example.musicalgames.games.flappy

import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName

const val LEN_INF = -1
const val DELIMITER = ","
object FlappyLevels {
    val baseLevels: List<TaggedLevel> = generateMajorLevels()
    private fun generateArcadeLevel(notes: List<Int>, root: Int, mode: String): TaggedLevel {
        val minPitch = notes[0]
        val maxPitch = notes[notes.size-1]

        val name = "${noteName(minPitch)} to ${noteName(maxPitch)}, root: ${noteLetter(root)} $mode"
        val description = "Arcade"
        val level = FlappyLevel(minPitch, maxPitch, root, notes, LEN_INF)
        return TaggedLevel(Game.FLAPPY, 0, name, description, level, isFavourite = false, isCustom = false)

    }

    private fun generateMajorLevels():List<TaggedLevel> {
        val levels = mutableListOf<TaggedLevel>()

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

}
