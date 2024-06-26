package com.example.musicalgames.game_activity

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application) {
    abstract var score: Int
    abstract fun setDataFromIntent(intent: Intent)

}