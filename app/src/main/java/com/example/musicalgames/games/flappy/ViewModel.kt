package com.example.musicalgames.games.flappy

import androidx.lifecycle.ViewModel

class ViewModel : ViewModel() {
    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: String = "G3"
    var maxRange: String = "G4"
}