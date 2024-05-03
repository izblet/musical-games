package com.example.musicalgames.controllers

import com.example.musicalgames.models.Bird
import com.example.musicalgames.models.PitchRecogniser

class BirdController(pitchRecogniser: PitchRecogniser) {
    //should be passed in the constructor
    var targetY:Float = 0f

    fun updatePosition(bird: Bird, maxCoordinate: Float) {
        //here get new frequency from pitch recogniser
        //then calculate target coordinates as values - the bird is going to be displayed in range (0,1)
        //coordinates of the bird will always be between 0 and 1
        //if the sound is too low or to high, the target value can be >1 or <0
        //the position of the bird display will be calculated (maxCoordinate*value)
        //when confidence is <0.9 the bird should not move at all
        targetY?.let {
            val deltaY = (it-bird.y)/10
            bird.y +=deltaY
        }
    }
}