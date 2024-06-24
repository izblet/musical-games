package com.example.musicalgames.game_activity

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.example.musicalgames.games.flappy.level_list.Level

interface GameIntentMaker {
    fun getIntent(activity: FragmentActivity, level: Level) : Intent
}