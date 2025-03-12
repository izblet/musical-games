package com.example.musicalgames.games.flappy

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser

class FlappyViewModel() : ViewModel(), GameViewModel{
    private var gameId: Int = GameMap.gameInfos[Game.FLAPPY]!!.id
    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: Int = midi("C3")
    var maxRange: Int = midi("C4")
    var root: Int = midi("C4")
    var endAfter: Int = LEN_INF
    var gapPositions: List<Int> = listOf()


    override fun setLevel(level: Level) {
        //should include a check
        val flappyLevel = level as FlappyLevel

        minRange = flappyLevel.minPitch
        maxRange = flappyLevel.maxPitch
        root = flappyLevel.root
        endAfter = flappyLevel.endAfter
        gapPositions = flappyLevel.keyList
    }

}