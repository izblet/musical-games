package com.example.musicalgames.game_activity

import android.content.Intent
import androidx.fragment.app.FragmentActivity

interface GameIntentMaker {
    fun getIntent(activity: FragmentActivity, level: Level) : Intent
}