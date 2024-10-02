package com.example.musicalgames.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.musicalgames.utils.MusicUtil.cleffIndexC4
import com.example.musicalgames.utils.Note


class StaffPainter(private val clefBitmap: Bitmap, private val sharpBitmap: Bitmap, private val flatBitmap: Bitmap, private val treble: Boolean) {
    private val staffLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
    }

    private val notePaint = Paint().apply {
        color = Color.BLACK
    }
    private var top:Float = 0f
    private var bottom:Float = 0f
    private var noteRadius = 0f
    private var lineSpacing = 0f
    private var height = 0f
    private var flats: List<Note>? = null
    private var sharps: List<Note>? = null
    fun setFlats(flats: List<Note>) {
        this.flats=flats
    }
    fun setSharps(sharps: List<Note>) {
        this.sharps=sharps
    }
    fun setConstraints(top: Float, bottom: Float) {
        this.top=top
        this.bottom=bottom
        height=(bottom-top)
        noteRadius=lineSpacing/2
        this.lineSpacing = (height)/4
    }


    private fun getNoteYPosition(clefIndex: Int): Float {
        val middleCPosition : Float =
            if(treble) top + 5 * lineSpacing
            else top - 3*lineSpacing/2

        return middleCPosition - clefIndex * (lineSpacing / 2)
    }

    fun drawStaff(canvas: Canvas, left: Float, width: Float, midiNotes: List<Int>) {
        drawStaffLines(canvas, left, width)
        drawClef(canvas, left)
        drawNotes(canvas, left + 150f, midiNotes)
    }

    private fun drawStaffLines(canvas: Canvas, left: Float, width: Float) {
        val startX = left
        val stopX = left + width
        for (i in 0..4) {
            val y = top + i * lineSpacing
            canvas.drawLine(startX, y, stopX, y, staffLinePaint)
        }
    }

    private fun drawClef(canvas: Canvas, left: Float) {
        val trebleClefWidth = height / 2
        val trebleClefHeight = clefBitmap.height * (trebleClefWidth / clefBitmap.width)
        val dstRect = RectF(left, top, left + trebleClefWidth, top + trebleClefHeight)
        canvas.drawBitmap(clefBitmap, null, dstRect, null)
    }

    private fun drawNotes(canvas: Canvas, left: Float, midiNotes: List<Int>) {
        val noteSpacing = 200f
        midiNotes.forEachIndexed { index, midiNote ->
            val clefIndex = cleffIndexC4(Note(midiNote))
            val y = getNoteYPosition(clefIndex)
            drawLedgerLinesIfNeeded(canvas, left + index * noteSpacing, y)
            canvas.drawCircle(left + index * noteSpacing, y, noteRadius, notePaint)
        }
    }

    private fun drawLedgerLinesIfNeeded(canvas: Canvas, x: Float, y: Float) {
        val bottomStaffLineY = top + 4 * lineSpacing
        val topStaffLineY = top
        val ledgerLineLength = 40f

        if (y > bottomStaffLineY + lineSpacing/2) {
            var currentY = bottomStaffLineY + lineSpacing
            while (currentY <= y) {
                canvas.drawLine(x - ledgerLineLength, currentY, x + ledgerLineLength, currentY, staffLinePaint)
                currentY += lineSpacing
            }
        } else if (y < topStaffLineY - lineSpacing/2) {
            var currentY = topStaffLineY - lineSpacing
            while (currentY >= y ) {
                canvas.drawLine(x - ledgerLineLength, currentY, x + ledgerLineLength, currentY, staffLinePaint)
                currentY -= lineSpacing
            }
        }
    }
}
