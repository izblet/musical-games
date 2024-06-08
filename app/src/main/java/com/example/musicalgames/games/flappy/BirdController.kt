package com.example.musicalgames.games.flappy

class BirdController(private val pitchRecogniser: PitchRecogniser) {
    //should be passed in the constructor
    var targetY:Float = 0f

    fun updatePosition(bird: Bird, maxCoordinate: Float) {
        var pitch = pitchRecogniser.getPitch()
        //pitch is -1 if does not exist because of an error or low confidence level
        //otherwise it is a number between 0 and 1, but with different semantics than that of tensorflow
        //0 is the bottom of the screen, 1 is the top of the screen
        if(pitch == -1f) {
            targetY = bird.y + 15
        }
        else {
            targetY = (1-pitch)*maxCoordinate //1-pitch is here because we are getting coordinates from top
        }

        //the position of the bird display will be calculated (maxCoordinate*value)
        targetY?.let {
            val deltaY = (it - bird.y) / 10
            if(bird.y+deltaY>0 && bird.y+deltaY<maxCoordinate)
                bird.y = bird.y+deltaY
        }
    }
}