package com.example.musicalgames.games.sight_sing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class StaffView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val staffLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
    }

    private val notePaint = Paint().apply {
        color = Color.BLACK
    }

    private val noteRadius = 20f

    private var notePositions: List<Float> = emptyList()

    fun setNotePositions(positions: List<Float>) {
        notePositions = positions
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawStaffLines(canvas)
        drawNotes(canvas)
    }

    private fun drawStaffLines(canvas: Canvas) {
        val lineSpacing = 100f
        val startX = 50f
        val stopX = width - 50f
        for (i in 0..4) {
            val y = 100f + i * lineSpacing
            canvas.drawLine(startX, y, stopX, y, staffLinePaint)
        }
    }

    private fun drawNotes(canvas: Canvas) {
        val startX = 150f
        val noteSpacing = 200f
        notePositions.forEachIndexed { index, y ->
            canvas.drawCircle(startX + index * noteSpacing, y, noteRadius, notePaint)
        }
    }
}
