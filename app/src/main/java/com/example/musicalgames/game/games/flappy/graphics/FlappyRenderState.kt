package com.example.musicalgames.game.games.flappy.graphics

import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

data class FlappyRenderState(
    val birdShape: Shape,
    val pipes: List<Triple<Rect, Rect?, Rect?>>,
    val score: Int,
    val gameEnded: Boolean
)
