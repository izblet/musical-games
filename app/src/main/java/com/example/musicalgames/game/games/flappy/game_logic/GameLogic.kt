package com.example.musicalgames.game.games.flappy.game_logic

import android.util.Log
import com.example.musicalgames.utils.geometry.Point
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

enum class GameEndReason {
    COLLISION,
    SCORE_REACHED
}

class GameLogic (
    private val bird : Bird, //the bird should be in the starting position
    private val gameRectMidiCoordinates: Rect,
    private val pipeDistance: Double,
    private val speed: Double, //pipe movement speed, in game-space units per second
    private val pipeGenerator: PipeGenerator,
    private val midiCoordinateController: MidiCoordinateController,
    private val endAfter: Int? = null
){
    var gameEnded = false
        private set
    var endReason: GameEndReason? = null
        private set
    var score = 0
        private set

    val pipes : MutableList<Pipe> = mutableListOf(getPipe())


    var currentPipeIndex:Int =0

    init {
        require(pipeDistance>0){"Pipe distance should be positive"}
        require(speed>0){"Speed should be positive"}
        require(bird.getShape().isContained(gameRectMidiCoordinates)) {"The bird is not contained in game rectangle"}
        require(pipeDistance<gameRectMidiCoordinates.right-gameRectMidiCoordinates.left){"At least one pipe should be in game screen at all times"}
        require(bird.getShape().getBoundingRectangle().width<pipeDistance){"The bird cannot be wider than the distance between pipes"}
        //maybe also some constraints on speed? I don't know what it's supposed to be exactly
        bird.setConstraints(gameRectMidiCoordinates)
    }
    fun getBirdShape(): Shape {
        return bird.getShape()
    }

    fun getPipeRects(): List<Triple<Rect, Rect?, Rect?>> {
        return pipes.map { Triple(it.getBoundingRectangle(), it.rectTop, it.rectBottom) }
    }

    //the i-th note here is the one the i-th pipe in getPipeRects() corresponds to
    fun getPipeNotes(): List<Int> {
        return pipes.map { it.holeMidi }
    }

    private fun getPipe() : Pipe {
        return pipeGenerator.getPipe(
            left=gameRectMidiCoordinates.right,
            bottomMidiCoordinate=gameRectMidiCoordinates.bottom,
            topMidiCoordinate=gameRectMidiCoordinates.top)
    }
    private fun movePipes(deltaTimeSeconds: Double) {
        //first move the existing pipes
        val moveVector = Point(-speed * deltaTimeSeconds, .0)
        pipes.forEach { it.moveByVector(moveVector) }

        //then update the current pipe, check if we should increase the score
        val birdRect=bird.getShape().getBoundingRectangle()
        fun passed(pipe: Pipe) =
            (!pipe.getBoundingRectangle().intersects(birdRect)) && pipe.getBoundingRectangle().left < birdRect.right

        if(passed(pipes[currentPipeIndex])) {
            currentPipeIndex++
            //and also here we discover that our score increases
            score++
        }

        //then remove invisible pipes, update the index accordingly
        val removedCount = pipes.count {it.getBoundingRectangle().right<gameRectMidiCoordinates.left}
        pipes.removeAll { it.getBoundingRectangle().right<gameRectMidiCoordinates.left }
        currentPipeIndex-=removedCount

        //then add pipes if necessary
        while(pipes.last().getBoundingRectangle().left <=gameRectMidiCoordinates.right - pipeDistance) {
            pipes.add(getPipe())
        }

    }

    fun tickFrame(deltaTimeSeconds: Double) {
        Log.d("LOOPS", "tick frame")
        bird.advance()
        movePipes(deltaTimeSeconds)

        val currentPipe = pipes[currentPipeIndex]

        //the rectangles can be null for the very top/bottom notes
        val collided = listOfNotNull(currentPipe.rectTop, currentPipe.rectBottom)
            .any { bird.intersects(it) }

        if (collided) {
            gameEnded = true
            endReason = GameEndReason.COLLISION
        }
        if (endAfter != null && score >= endAfter) {
            gameEnded = true
            endReason = GameEndReason.SCORE_REACHED
        }
    }
    fun tickBird() {
        Log.d("LOOPS","tick bird")
        val pitch = midiCoordinateController.getCoordinate()
        if(pitch!=null)
            bird.setTarget(pitch)
    }

}