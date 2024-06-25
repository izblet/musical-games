package com.example.musicalgames.games.flappy

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.musicalgames.utils.MusicUtil

class Pipe(
    color: Int,
    var x: Float,
    private val gap: Int,
    private val minVisible: Double,
    private val maxVisible: Double,
    private val pitchSize: Float
) {
    //x is the coordinate of left side of the pipe, gap is the coordinate of the middle of the gap
    companion object {
        const val WIDTH = 0.05f
        const val SPEED = 0.002f
        const val PIPE_SPACE = 0.3f//space between pipes
    }

    private val paint = Paint()
    init {
        paint.color=color
    }

    fun move() {
        x -= SPEED
    }

    private fun getKeyRect(note: Int, whiteColor: Int, blackColor: Int): Pair<RectF, Int> {
        val leftEnd = x;
        val rightEnd = x + WIDTH
        val halfKey = pitchSize/2
        val keyStart =
            (1f - MusicUtil.normalize(note, minVisible, maxVisible)).toFloat() + halfKey
        val keyEnd = keyStart - 2*halfKey

        val color = if (MusicUtil.isWhite(note)) whiteColor else blackColor
        return RectF(leftEnd, keyEnd, rightEnd, keyStart) to color

    }
    private fun getGapKeys(): List<Pair<RectF, Int>> {
        val keys = mutableListOf<Pair<RectF, Int>>()
        val note = gap
        val halfKey = pitchSize/2

        //TODO: the colors should come from somewhere else
        keys.add(getKeyRect(note,
            Color.argb(50,250, 250, 250),
            Color.argb(50,0,0,0)))
        return keys
    }
    private fun getTopKeys(): List<Pair<RectF, Int>> {
        val keys = mutableListOf<Pair<RectF, Int>>()
        val halfKey = pitchSize/2

        var note = gap+1;//the key that is currently drawn

        while(MusicUtil.spice(note)-halfKey<maxVisible) {
            keys.add(getKeyRect(note, Color.WHITE, Color.BLACK))
            note++
        }

        return keys
    }
    private fun getBottomKeys(): List<Pair<RectF, Int>> {
        val keys = mutableListOf<Pair<RectF, Int>>()

        val halfKey = pitchSize/2
        var note = gap-1;//the key that is currently drawn

        while(MusicUtil.spice(note)+halfKey>minVisible) {
            keys.add(getKeyRect(note, Color.WHITE, Color.BLACK))
            note--
        }

        return keys
    }
    fun getTopRect(): RectF {
        val nextNote = gap+1;
        val topEnd = 1f -MusicUtil.normalize(nextNote, minVisible, maxVisible)+pitchSize/2
        val leftEnd = x;
        val rightEnd = x+ WIDTH
        return RectF(leftEnd, 0f, rightEnd, topEnd.toFloat())
    }
    fun getBottomRect(): RectF {
        val prevNote = gap-1;
        val bottomEnd = 1f - MusicUtil.normalize(prevNote, minVisible, maxVisible) - pitchSize/2
        val leftEnd = x
        val rightEnd = x + WIDTH
        return RectF(leftEnd, bottomEnd.toFloat(), rightEnd, 1f)
    }
    private fun scale(rect: RectF, scaleX:Float, scaleY:Float) {
        rect.left *=scaleX
        rect.top *=scaleY
        rect.right*=scaleX
        rect.bottom*=scaleY
    }

    fun draw(canvas: Canvas, screenHeight: Float, screenWidth: Float) {
        //val topRect = getTopRect()
        //scale(topRect, screenWidth, screenHeight)
        //val bottomRect = getBottomRect()
        //scale(bottomRect, screenWidth, screenHeight)
        //paint.color = Color.GRAY
        //canvas.drawRect(topRect, paint)
        //canvas.drawRect(bottomRect, paint)


        val topKeys = getTopKeys()
        val bottomKeys = getBottomKeys()
        val gapKeys = getGapKeys()
        val allKeys = topKeys+bottomKeys+gapKeys

        allKeys.forEach{ (rect, color)->
            scale(rect, screenWidth, screenHeight)
            paint.color = color
            canvas.drawRect(rect, paint)
        }
    }

    fun passedLastPosition() : Boolean {
        return x < (1f- PIPE_SPACE)
    }
}