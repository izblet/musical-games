package com.example.musicalgames.games.flappy

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
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
        const val TYPE_STR = "type"
        const val MIN_STR = "min"
        const val MAX_STR = "max"
        const val ROOT_STR = "root"
        const val END_STR = "end"
        const val POSITIONS_STR = "positions"
        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is FlappyLevel)
                throw Exception("level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra(MIN_STR, level.minPitch)
                putExtra(MAX_STR, level.maxPitch)
                putExtra(ROOT_STR, level.root)
                putExtra(END_STR, level.endAfter)
                val keyList = ArrayList(level.keyList)
                putExtra(POSITIONS_STR, keyList)
            }
        }
    }
    override fun setDataFromIntent(intent: Intent) {
        val type = intent.getStringExtra(TYPE_STR)

        minRange = intent.getIntExtra(MIN_STR, minRange)
        maxRange = intent.getIntExtra(MAX_STR, maxRange)
        root = intent.getIntExtra(ROOT_STR, root)
        endAfter = intent.getIntExtra(END_STR, endAfter)
        val positions = intent.getIntegerArrayListExtra(POSITIONS_STR)
        gapPositions = positions!!
    }
    //private val highScoreDao: HighScoreDao = GameDatabase.getDatabase(application).highScoreDao()

    suspend fun checkHighScore(): Boolean {
        /*
        val highScores: List<HighScore> = highScoreDao.getHighScores(gameId,
        )

        highScoreDao.insertAndTrim(HighScore(gameId = gameId, modeId = gameType!!.toString(), score = score))

        return if (highScores.isNotEmpty())
            score > highScores[0].score

        else true
         */
        return false
    }

}