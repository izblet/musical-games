package com.example.musicalgames.game_activity

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel

//TODO: this class should be removed
interface IntentSettable {
    fun setDataFromIntent(intent: Intent)

}