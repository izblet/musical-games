package com.example.musicalgames.games.flappy

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game_activity.IntentSettable
import com.example.musicalgames.games.Game
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser

class FlappyViewModel() : ViewModel(), IntentSettable{
    private var gameId: Int = GameMap.gameInfos[Game.FLAPPY]!!.id
    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: Int = midi("C3")
    var maxRange: Int = midi("C4")
    var root: Int = midi("C4")
    var endAfter: Int = LEN_INF
    var gapPositions: List<Int> = listOf()

    companion object : GameIntentMaker {

        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is FlappyLevel)
                throw Exception("level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra("level", level)
            }
        }
    }
    override fun setDataFromIntent(intent: Intent) {
        val level = intent.getParcelableExtra<FlappyLevel>("level")

        minRange = level!!.minPitch
        maxRange = level.maxPitch
        root = level.root
        endAfter = level.endAfter
        gapPositions = level.keyList
    }

}