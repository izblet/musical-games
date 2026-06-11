package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.utils.geometry.Point
import com.example.musicalgames.utils.geometry.Rect

class Pipe(
    topMidiCoordinate: Double,
    bottomMidiCoordinate: Double,
    left: Double,
    right: Double,
    val holeMidi: Int,
    holePadding: Double = 0.0
) {
    private var rect = Rect(left = left, top = topMidiCoordinate, right = right, bottom = bottomMidiCoordinate)

    //for max and min midi-holes in the range there can be no top/bottom pipe, and the rectangle is null
    var rectTop: Rect?
        private set
    var rectBottom: Rect?
        private set

    init {
        //a key for midi note m spans [m - 0.5, m + 0.5] in midi coordinates
        val holeBottom = holeMidi - 0.5 - holePadding
        val holeTop = holeMidi + 0.5 + holePadding

        rectTop = if (holeTop < topMidiCoordinate) Rect(left = left, top = topMidiCoordinate, right = right, bottom = holeTop) else null
        rectBottom = if (holeBottom > bottomMidiCoordinate) Rect(left = left, top = holeBottom, right = right, bottom = bottomMidiCoordinate) else null
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