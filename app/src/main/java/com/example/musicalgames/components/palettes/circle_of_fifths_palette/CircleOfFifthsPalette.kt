package com.example.musicalgames.components.palettes.circle_of_fifths_palette
import android.content.Context
import android.graphics.*
import android.health.connect.datatypes.units.Length
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
    private val axisPaintMinor = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val axisPaintMajor = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
        isAntiAlias = true

    }

    private val totalSegments = 12
    private val gapAngle = 3f  // gap between wedges (in degrees) - to be removed when drawing is fixed
    private val separation = 6f  // gap between major and minor rings
    private val middleHoleRadiusProportion = 0.2f
    private val minorRadiusProportion = 0.6f


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val outerRadius = min(width, height)*0.5f
        val majorInnerRadius = outerRadius * minorRadiusProportion + separation
        val minorOuterRadius = outerRadius * minorRadiusProportion
        val minorInnerRadius = outerRadius * middleHoleRadiusProportion

        val separationLengthToAngleLength : (Float) -> Float = {
            radius: Float ->
                val circleLength = 2* PI * radius
                (360*separation/circleLength).toFloat()
        }

        val gapAngleOuter = separationLengthToAngleLength(outerRadius)
        val gapAngleMajorInner = separationLengthToAngleLength(majorInnerRadius)
        val gapAngleMinorOuter = separationLengthToAngleLength(minorOuterRadius)
        val gapAngleMinorInner = separationLengthToAngleLength(minorInnerRadius)

        val pieceAngleLength = 360f/totalSegments
        val rotationOffset = -pieceAngleLength/2f //so that C is on the top


        for (i in 0 until totalSegments) {

            val pieceOffset = rotationOffset + i*pieceAngleLength
            var paint = if (i%3==0) axisPaintMajor else paintMajor

            drawSegment(canvas, centerX, centerY,
                majorInnerRadius, outerRadius,
                startAngleInner = pieceOffset + gapAngleMajorInner, startAngleOuter = pieceOffset + gapAngleOuter,
                sweepAngleInner = pieceAngleLength - gapAngleMajorInner, sweepAngleOuter = pieceAngleLength - gapAngleOuter,
                paint)

            paint = if (i%3==0) axisPaintMinor else paintMinor
            drawSegment(canvas, centerX, centerY,
                minorInnerRadius, minorOuterRadius,
                startAngleInner = pieceOffset + gapAngleMinorInner, startAngleOuter = pieceOffset + gapAngleMinorOuter,
                sweepAngleInner = pieceAngleLength - gapAngleMinorInner, sweepAngleOuter = pieceAngleLength - gapAngleMinorOuter,
                paint)
        }
    }

    private fun drawSegment(
        canvas: Canvas,
        cx: Float, cy: Float,
        innerRadius: Float, outerRadius: Float,
        startAngleInner: Float, startAngleOuter: Float,
        sweepAngleInner: Float, sweepAngleOuter: Float,
        paint: Paint
    ) {
        val path = Path()
        val startRadInner = Math.toRadians(startAngleInner.toDouble())
        val startRadOuter = Math.toRadians(startAngleOuter.toDouble())
        val endRadInner = Math.toRadians((startAngleInner + sweepAngleInner).toDouble())
        //val endRadOuter = Math.toRadians((startAngleOuter + sweepAngleOuter).toDouble())

        val x1Outer = cx + outerRadius * cos(startRadOuter).toFloat()
        val y1Outer = cy + outerRadius * sin(startRadOuter).toFloat()

        val x1Inner = cx + innerRadius * cos(startRadInner).toFloat()
        val y1Inner = cy + innerRadius * sin(startRadInner).toFloat()

        val x2Inner = cx + innerRadius * cos(endRadInner).toFloat()
        val y2Inner = cy + innerRadius * sin(endRadInner).toFloat()

        path.moveTo(x1Inner, y1Inner)
        path.lineTo(x1Outer, y1Outer)
        path.arcTo(RectF(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius), startAngleOuter, sweepAngleOuter)
        path.lineTo(x2Inner, y2Inner)
        path.arcTo(RectF(cx - innerRadius, cy - innerRadius, cx + innerRadius, cy + innerRadius), startAngleInner + sweepAngleInner, -sweepAngleInner)
        path.close()

        canvas.drawPath(path, paint)
    }
}