package com.example.musicalgames.game.games.flappy

import com.example.musicalgames.utils.geometry.Rect
import com.example.musicalgames.utils.geometry.Shape

data class FlappyRenderState(
    val birdShape: Shape,
    val pipes: List<Pair<Rect?, Rect?>>,
    val score: Int,
    val gameEnded: Boolean
)
