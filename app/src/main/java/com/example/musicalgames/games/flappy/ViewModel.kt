package com.example.musicalgames.games.flappy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.HighScore
import com.example.musicalgames.games.HighScoreDao
import com.example.musicalgames.games.flappy.level_list.LEN_INF
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GameOption
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser

class ViewModel(application: Application) : AndroidViewModel(application) {
    private var gameId: Int = GameMap.gameInfos[Game.FLAPPY]!!.id
    var gameType: GameOption? = null //type of game - levels/custom/arcade - just for inserting into the database
    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: String = "G3"
    var maxRange: String = "G4"
    var endAfter: Int = LEN_INF


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