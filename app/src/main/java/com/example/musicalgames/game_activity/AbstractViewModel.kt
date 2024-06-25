package com.example.musicalgames.game_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application) {
    abstract var score: Int

}