package com.example.musicalgames.games.mental_intervals

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.example.musicalgames.game_activity.AbstractViewModel
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.Note

class MentalViewModel(application: Application) : AbstractViewModel(application) {
    companion object : GameIntentMaker {
        enum class Extra {
            MAX_INTERVAL
        }
        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is MentalLevel)
                throw Exception("Level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra(Extra.MAX_INTERVAL.name,level.maxSemitoneInterval)
            }
        }

    }
    override fun setDataFromIntent(intent: Intent) {
        maxInterval = intent.getIntExtra(Extra.MAX_INTERVAL.name, 5)

    }

    var maxInterval: Int = Int.MAX_VALUE
    override var score: Int = 0
}