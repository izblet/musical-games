package com.example.musicalgames.components

import android.graphics.Color
import android.graphics.Paint

object Paints {
    val whiteTextPaint = Paint().apply {
        textSize = 70f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }
    val blackTextPaint = Paint().apply {
        textSize = 70f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
    }
    val whiteFillPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    val blackFillPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    val whiteStrokePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    val blackStrokePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
}