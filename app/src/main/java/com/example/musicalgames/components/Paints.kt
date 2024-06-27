package com.example.musicalgames.components

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.example.musicalgames.R

object Paints {
    fun getWhiteTextPaint(context: Context): Paint {
        return Paint().apply {
            textSize = 70f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = Color.WHITE
        }
    }

    fun getBlackTextPaint(context: Context): Paint {
        return Paint().apply {
            textSize = 70f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = Color.BLACK
        }
    }
    fun getWhiteFillPaint(context: Context): Paint {
        return Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }
    fun getBlackFillPaint(context: Context) : Paint {
        return Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }
    fun getRedFillPaint(context: Context) : Paint {
        return Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    }
    fun getBlueFillPaint(context: Context) : Paint {
        return Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context,R.color.blue)
            style = Paint.Style.FILL
        }
    }
    fun getWhiteStrokePaint(context: Context) : Paint {
        return Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
    }
    fun getBlackStrokePaint(context: Context) : Paint {
        return Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
    }
}