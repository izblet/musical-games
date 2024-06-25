package com.example.musicalgames.games.play_by_ear

import android.util.Log
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.flappy.FlappyLevel
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.frequency
import com.example.musicalgames.utils.MusicUtil.getWhiteKeysFrom
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName
import com.example.musicalgames.utils.MusicUtil.noteoctave

object EarPlayLevels {
    val baseLevels: List<PlayEarLevel> = generateLevels()
    private fun generateLevel(notes: List<Int>, notesNum: Int) : PlayEarLevel {
        val id =0
        val interval = Int.MAX_VALUE
        val root = midi("C4")
        val span = notes[notes.size-1]-notes[0]
        //if the span is at least an octave, we just show the note
        //if the notes end on the root, we show the octave below
        //otherwise we show the octave above
        val min =
            if(span<12 || noteLetter(notes[notes.size-1]) == noteLetter(root))
                midi(noteLetter(root) + noteoctave(notes[0]))
            else notes[0]

        val max =
            if(span>=12 || noteLetter(notes[notes.size-1]) == noteLetter(root))
                notes[notes.size-1]
            else midi(noteLetter(root)+ (noteoctave(notes[notes.size-1])+1))

        return PlayEarLevel(
            id,
            min,
            max,
            root,
            notesNum,
            interval,
            notes.toList(),
            "C major, ${noteName(notes[0])} to ${noteName(notes[notes.size-1])}",
            "$notesNum notes"
        )
    }
    private fun generateLevels(): List<PlayEarLevel> {
        val levels = mutableListOf<PlayEarLevel>()
        val rootNote = midi("C4")

        for(notesNum in listOf(3,5,7)) {
            for (sizeList in listOf(listOf(3, 4), listOf(5, 6), listOf(7), listOf(8))) {
                //generate levels with notes above root
                for (size in sizeList) {
                    val notes = getWhiteKeysFrom(rootNote, size)
                    levels.add(generateLevel(notes, notesNum))
                }
                //generate levels with notes below root
                for (size in sizeList) {
                    val notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                    levels.add(generateLevel(notes, notesNum))
                }

                //generate levels that concat notes below and notes above
                for (size in sizeList) {
                    var notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                    notes = notes.slice(0..(notes.size - 2)) + getWhiteKeysFrom(
                        rootNote,
                        size
                    )
                    levels.add(generateLevel(notes, notesNum))
                }
            }
        }

        return levels
    }

}

data class PlayEarLevel (
    override val id: Int,
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val notesNum: Int,
    val maxSemitoneInterval: Int,
    val keyList: List<Int>,
    override val name: String,
    override val description: String,
): Level(id, name, description)
