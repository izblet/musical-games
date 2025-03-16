package com.example.musicalgames.components

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.example.musicalgames.R

object ComponentPaints {
    //TODO: the colours should be resources and have names like white_key_colour etc
    //and function names should be more descriptive, the don't just return white/black etc, they return the keyboard colours

    private fun getFillPaint(paintColor: Int) : Paint {
        return Paint().apply {
            isAntiAlias = true
            color = paintColor
            style = Paint.Style.FILL
        }
    }

    private fun getTextPaint(paintColor: Int) : Paint {
        return Paint().apply {
            textSize = 70f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = paintColor
        }
    }

    fun getWhiteTextPaint(context: Context): Paint {
        return getTextPaint(Color.WHITE)
    }

    fun getBlackTextPaint(context: Context): Paint {
        return getTextPaint(Color.BLACK)
    }
    fun getWhiteFillPaint(context: Context): Paint {
        return getFillPaint(Color.WHITE)
    }

    fun getBlackFillPaint(context: Context) : Paint {
        return getFillPaint(Color.BLACK)
    }
    fun getLightgrayFillPaint(context: Context) : Paint {
        return getFillPaint(Color.GRAY)
    }
    fun getDarkgrayFillPaint(context: Context) : Paint {
        return getFillPaint(Color.DKGRAY)
    }
    fun getRedFillPaint(context: Context) : Paint {
        return getFillPaint(Color.RED)
    }
    fun getBlueFillPaint(context: Context) : Paint {
        return getFillPaint(ContextCompat.getColor(context,R.color.blue))
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