package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.utils.geometry.Point
import com.example.musicalgames.utils.geometry.Rect

class Pipe(
    top: Double,
    bottom: Double,
    left: Double,
    right: Double,
    minMidi: Int,
    maxMidi: Int,
    holeMidi: Int,
    holePadding: Double = 0.0
) {
    private var rect = Rect(left = left, top = top, right = right, bottom = bottom)

    //for max and min midi-holes in the range there can be no top/bottom pipe, and the rectangle is null
    var rectTop: Rect?
        private set
    var rectBottom: Rect?
        private set

    init {

        val slotHeight = (top - bottom) / (maxMidi - minMidi + 1)
        val holeIndex = holeMidi - minMidi

        val holeBottom = bottom + holeIndex * slotHeight - holePadding
        val holeTop = bottom + (holeIndex + 1) * slotHeight + holePadding


        rectTop = if (holeTop < top) Rect(left = left, top = top, right = right, bottom = holeTop) else null
        rectBottom = if (holeBottom > bottom) Rect(left = left, top = holeBottom, right = right, bottom = bottom) else null
    }
    fun moveByVector(vector: Point) {
        rect = rect.getTranslated(vector)
        rectTop = rectTop?.getTranslated(vector)
        rectBottom = rectBottom?.getTranslated(vector)
    }

    fun getBoundingRectangle(): Rect {
        return rect
    }
}