package com.example.musicalgames.games.flappy

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.HighScore
import com.example.musicalgames.games.HighScoreDao
import com.example.musicalgames.games.flappy.level_list.LEN_INF
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameIntentMaker
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GameOption
import com.example.musicalgames.games.flappy.level_list.FlappyLevel
import com.example.musicalgames.games.flappy.level_list.Level
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser

class ViewModel(application: Application) : AndroidViewModel(application) {
    private var gameId: Int = GameMap.gameInfos[Game.FLAPPY]!!.id
    var gameType: GameOption? = null //type of game - levels/custom/arcade - just for inserting into the database
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

            val flappyLevel = level as FlappyLevel

            return Intent(activity, ActivityFlappy::class.java).apply {
                if(flappyLevel.endAfter == LEN_INF)
                    putExtra(TYPE_STR, GameOption.ARCADE.name)
                else
                    putExtra(TYPE_STR, GameOption.LEVELS.name)
                putExtra(MIN_STR, flappyLevel.minPitch)
                putExtra(MAX_STR, flappyLevel.maxPitch)
                putExtra(ROOT_STR, flappyLevel.root)
                putExtra(END_STR, flappyLevel.endAfter)
                val keyList = ArrayList(flappyLevel.keyList)
                putExtra(POSITIONS_STR, keyList)
            }
        }
    }
    fun setDataFromExtra(intent: Intent) {
        val type = intent.getStringExtra(TYPE_STR)
        gameType = GameOption.valueOf(type ?: GameOption.ARCADE.name)

        minRange = intent.getIntExtra(MIN_STR, minRange)
        maxRange = intent.getIntExtra(MAX_STR, maxRange)
        root = intent.getIntExtra(ROOT_STR, root)
        endAfter = intent.getIntExtra(END_STR, endAfter)
        val positions = intent.getIntegerArrayListExtra(POSITIONS_STR)
        gapPositions = positions!!
    }

    private val highScoreDao: HighScoreDao = GameDatabase.getDatabase(application).highScoreDao()

    suspend fun checkHighScore(): Boolean {
        val highScores: List<HighScore> = highScoreDao.getHighScores(gameId,
            gameType!!.toString()
        )

        highScoreDao.insertAndTrim(HighScore(gameId = gameId, modeId = gameType!!.toString(), score = score))

        return if (highScores.isNotEmpty())
            score > highScores[0].score

        else true
    }

}