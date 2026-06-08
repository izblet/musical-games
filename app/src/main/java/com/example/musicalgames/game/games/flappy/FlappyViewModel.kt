package com.example.musicalgames.games.flappy

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Note
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser

class FlappyViewModel() : ViewModel() {
    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: Int = Note(ChromaticNote.C, 3).midiCode
    var maxRange: Int = Note(ChromaticNote.C, 4).midiCode
    var root: Int = Note(ChromaticNote.C, 4).midiCode
    var endAfter: Int = LEN_INF
    var gapPositions: List<Int> = listOf()


    fun setLevel(level: Level) {
        //should include a check
        val flappyLevel = level as FlappyLevel

        minRange = flappyLevel.minPitch
        maxRange = flappyLevel.maxPitch
        root = flappyLevel.root
        endAfter = flappyLevel.endAfter
        gapPositions = flappyLevel.keyList
    }

}