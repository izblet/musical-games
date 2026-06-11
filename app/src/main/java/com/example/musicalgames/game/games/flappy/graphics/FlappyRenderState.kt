package com.example.musicalgames.game.games.flappy.graphics

import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

data class FlappyRenderState(
    val birdShape: Shape,
    val pipes: List<Triple<Rect, Rect?, Rect?>>,
    //the i-th note here is the one the i-th pipe in `pipes` corresponds to
    val pipeNotes: List<Int>,
    val score: Int,
    val gameEnded: Boolean
)