package com.example.musicalgames.components.palettes.circle_of_fifths_palette
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*


class CircleOfFifthsPalette(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintMajor = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val paintMinor = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val totalSegments = 12
    private val gapAngle = 3f  // gap between wedges (in degrees)
    private val ringSeparation = 12f  // gap between major and minor rings

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val outerRadius = min(width, height)*0.5f
        val innerRadius = outerRadius * 0.65f
        val minorOuterRadius = innerRadius - ringSeparation
        val minorInnerRadius = minorOuterRadius * 0.4f

        val pieceAngleLength = 360f/totalSegments
        val segmentAngleLength = pieceAngleLength - gapAngle
        val rotationOffset = pieceAngleLength/2f //so that c is on the top

        for (i in 0 until totalSegments) {
            val startAngle = rotationOffset + i * pieceAngleLength + gapAngle / 2

            drawSegment(canvas, centerX, centerY, innerRadius, outerRadius, startAngle, segmentAngleLength, paintMajor)

            drawSegment(canvas, centerX, centerY, minorInnerRadius, minorOuterRadius, startAngle, segmentAngleLength, paintMinor)
        }
    }

    private fun drawSegment(
        canvas: Canvas,
        cx: Float, cy: Float,
        innerRadius: Float, outerRadius: Float,
        startAngle: Float, sweepAngle: Float,
        paint: Paint
    ) {
        val path = Path()
        val startRad = Math.toRadians(startAngle.toDouble())
        val endRad = Math.toRadians((startAngle + sweepAngle).toDouble())

        val x1Outer = cx + outerRadius * cos(startRad).toFloat()
        val y1Outer = cy + outerRadius * sin(startRad).toFloat()

        val x1Inner = cx + innerRadius * cos(startRad).toFloat()
        val y1Inner = cy + innerRadius * sin(startRad).toFloat()

        val x2Inner = cx + innerRadius * cos(endRad).toFloat()
        val y2Inner = cy + innerRadius * sin(endRad).toFloat()

        path.moveTo(x1Inner, y1Inner)
        path.lineTo(x1Outer, y1Outer)
        path.arcTo(RectF(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius), startAngle, sweepAngle)
        path.lineTo(x2Inner, y2Inner)
        path.arcTo(RectF(cx - innerRadius, cy - innerRadius, cx + innerRadius, cy + innerRadius), startAngle + sweepAngle, -sweepAngle)
        path.close()

        canvas.drawPath(path, paint)
    }
}