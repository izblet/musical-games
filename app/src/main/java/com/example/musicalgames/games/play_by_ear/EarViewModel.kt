package com.example.musicalgames.games.play_by_ear

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.Note

class EarViewModel : ViewModel() {
    companion object : GameIntentMaker {
        enum class Extra {
            MIN_KEY,
            MAX_KEY,
            ROOT,
            AVAILABLE,
            MAX_INTERVAL
        }
        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is PlayEarLevel)
                throw Exception("Level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra(Extra.MAX_KEY.name, level.maxPitch)
                putExtra(Extra.MIN_KEY.name, level.minPitch)
                putExtra(Extra.AVAILABLE.name, ArrayList(level.keyList))
                putExtra(Extra.ROOT.name, level.root)
                putExtra(Extra.MAX_INTERVAL.name,level.maxSemitoneInterval)
            }
        }

    }
    fun setDataFromIntent(intent: Intent) {
        val min = intent.getIntExtra(Extra.MIN_KEY.name, midi("C4"))
        minKey = Note(min)
        val max = intent.getIntExtra(Extra.MAX_KEY.name, midi("C4"))
        maxKey = Note(max)
        root = intent.getIntExtra(Extra.ROOT.name, midi("C4"))
        maxInterval = intent.getIntExtra(Extra.MAX_INTERVAL.name, 5)
        val availableArray = intent.getIntegerArrayListExtra(Extra.AVAILABLE.name)
        available = availableArray!!.map{ Note(it) }

    }

    var minKey: Note? = null
    var maxKey: Note? = null
    var available: List<Note> = listOf()
    var maxInterval: Int = Int.MAX_VALUE
    var root: Int = midi("C4")
}