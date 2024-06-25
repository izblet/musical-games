package com.example.musicalgames.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.musicalgames.utils.MusicUtil.cleffIndexC4
import com.example.musicalgames.utils.Note


class StaffPainter(private val clefBitmap: Bitmap, private val treble: Boolean) {
    private val staffLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
    }

    private val notePaint = Paint().apply {
        color = Color.BLACK
    }

    private val noteRadius = 20f
    private val lineSpacing = 40f // Vertical distance between staff lines

    private fun getNoteYPosition(clefIndex: Int, top: Float): Float {
        val middleCPosition =
            if(treble) top + 5 * lineSpacing
            else top - lineSpacing
        return middleCPosition - clefIndex * (lineSpacing / 2)
    }

    fun drawStaff(canvas: Canvas, left: Float, top: Float, width: Float, height: Float, midiNotes: List<Int>) {
        drawStaffLines(canvas, left, top, width, height)
        drawClef(canvas, left, top, height)
        drawNotes(canvas, left + 150f, top, midiNotes)
    }

    private fun drawStaffLines(canvas: Canvas, left: Float, top: Float, width: Float, height: Float) {
        val startX = left
        val stopX = left + width
        for (i in 0..4) {
            val y = top + i * lineSpacing
            canvas.drawLine(startX, y, stopX, y, staffLinePaint)
        }
    }

    private fun drawClef(canvas: Canvas, left: Float, top: Float, height: Float) {
        val trebleClefWidth = height / 2
        val trebleClefHeight = clefBitmap.height * (trebleClefWidth / clefBitmap.width)
        val dstRect = RectF(left, top, left + trebleClefWidth, top + trebleClefHeight)
        canvas.drawBitmap(clefBitmap, null, dstRect, null)
    }

    private fun drawNotes(canvas: Canvas, left: Float, top: Float, midiNotes: List<Int>) {
        val noteSpacing = 200f
        midiNotes.forEachIndexed { index, midiNote ->
            val clefIndex = cleffIndexC4(Note(midiNote))
            val y = getNoteYPosition(clefIndex, top)
            drawLedgerLinesIfNeeded(canvas, left + index * noteSpacing, y, top)
            canvas.drawCircle(left + index * noteSpacing, y, noteRadius, notePaint)
        }
    }

    private fun drawLedgerLinesIfNeeded(canvas: Canvas, x: Float, y: Float, top: Float) {
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
