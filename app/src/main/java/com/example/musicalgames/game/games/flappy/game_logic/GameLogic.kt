package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.utils.geometry.Point
import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

class GameLogic (
    private val bird : Bird, //the bird should be in the starting position
    private val gameRect: Rect,
    private val pipeDistance: Double,
    private val speed: Double,
    private val minDisplayMidi: Int,
    private val maxDisplayMidi: Int,
    private val pipeGenerator: PipeGenerator,
    private val midiCoordinateController: MidiCoordinateController
){
    var gameEnded = false
        private set
    var score = 0
        private set

    val pipes : MutableList<Pipe> = mutableListOf(getPipe())


    var currentPipeIndex:Int =0

    init {
        require(pipeDistance>0){"Pipe distance should be positive"}
        require(speed>0){"Speed should be positive"}
        require(bird.getShape().isContained(gameRect)) {"The bird is not contained in game rectangle"}
        require(pipeDistance<gameRect.right-gameRect.left){"At least one pipe should be in game screen at all times"}
        require(bird.getShape().getBoundingRectangle().width<pipeDistance){"The bird cannot be wider than the distance between pipes"}
        //maybe also some constraints on speed? I don't know what it's supposed to be exactly
        bird.setConstraints(gameRect)
    }
    fun getBirdShape(): Shape {
        return bird.getShape()
    }

    fun getPipeRects(): List<Pair<Rect?, Rect?>> {
        return pipes.map { it.rectTop to it.rectBottom }
    }

    private fun getPipe() : Pipe {
        return pipeGenerator.getPipe(
            left=gameRect.right,
            bottom=gameRect.bottom,
            top=gameRect.top,
            minDisplayMidi=minDisplayMidi,
            maxDisplayMidi=maxDisplayMidi)
    }
    private fun movePipes() {
        //first move the existing pipes
        val moveVector = Point(-speed, .0)
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
        val removedCount = pipes.count {it.getBoundingRectangle().right<gameRect.left}
        pipes.removeAll { it.getBoundingRectangle().right<gameRect.left }
        currentPipeIndex-=removedCount

        //then add pipes if necessary
        while(pipes.last().getBoundingRectangle().left <=gameRect.right - pipeDistance) {
            pipes.add(getPipe())
        }

    }


    fun tickFrame() {
        val targetY = midiCoordinateController.getCoordinate()
        targetY?.let { bird.move(it) }
        movePipes()

        val currentPipe = pipes[currentPipeIndex]

        //the rectangles can be null for the very top/bottom notes
        val collided = listOfNotNull(currentPipe.rectTop, currentPipe.rectBottom)
            .any { bird.intersects(it) }

        if (collided) {
            gameEnded = true
        }
    }

}