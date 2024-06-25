package com.example.musicalgames.games.play_by_ear

import android.util.Log
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil.frequency
import com.example.musicalgames.utils.MusicUtil.getWhiteKeysFrom
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteName

object EarPlayLevels {
    val baseLevels: List<PlayEarLevel> = generateLevels()
    private fun generateLevels(): List<PlayEarLevel> {
        val levels = mutableListOf<PlayEarLevel>()
        val id =0
        val min = midi("C4")
        val max = midi("C5")
        val root = midi("C4")
        val notesNum = 3
        val interval = Int.MAX_VALUE
        val whitenotes = getWhiteKeysFrom(min, 8)
        val notes = mutableListOf(min, min+1)
        var nextNote = 2

        while(nextNote!=whitenotes.size) {
            notes.add(whitenotes[nextNote])
            nextNote++
            levels.add(
                PlayEarLevel(
                    id,
                    min,
                    max,
                    root,
                    notesNum,
                    interval,
                    notes,
                    "C major, ${notes.size} notes from C",
                    ""
                )
            )


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
