package com.example.musicalgames.components.palettes.circle_of_fifths_palette
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.musicalgames.components.ComponentPaints
import java.lang.Math.toDegrees
import kotlin.math.*
open class CircleOfFifthsPaletteView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

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
    private var _axisPaintMinor :Paint? = null
    private val axisPaintMinor get() = _axisPaintMinor!!
    private var _axisPaintMajor :Paint? = null
    private val axisPaintMajor get() = _axisPaintMajor!!

    init {
        _axisPaintMinor = ComponentPaints.getBlueFillPaint(context)
        _axisPaintMajor = ComponentPaints.getBlueFillPaint(context)
    }

    private var hasMinor = false

    private val totalSegments = 12
    private val separation = 6f  // gap between major and minor rings
    private val middleHoleRadiusProportion = 0.2f
    private var minorRadiusProportion = 0.5f

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f

    private var majorOuterRadius = 0f
    private var majorInnerRadius = 0f
    private var minorOuterRadius = 0f
    private var minorInnerRadius = 0f


    private val pieceAngleLength = 360f/totalSegments
    private val rotationOffset = -pieceAngleLength/2f //so that C is on the top

    fun hasMinor():Boolean {
        return hasMinor
    }
    fun showMinor(show: Boolean) {
        minorRadiusProportion = if(show) {
            0.5f
        } else {
            0.65f
        }
        hasMinor=show
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        centerX = width / 2f
        centerY = height / 2f
        radius = min(width,height)/2f

        majorOuterRadius = radius
        majorInnerRadius = majorOuterRadius * minorRadiusProportion + separation
        minorOuterRadius = majorOuterRadius * minorRadiusProportion
        minorInnerRadius = majorOuterRadius * middleHoleRadiusProportion

        val separationLengthToAngleLength : (Float) -> Float = {
            radius: Float ->
                val circleLength = 2* PI * radius
                (360*separation/circleLength).toFloat()
        }

        val gapAngleOuter = separationLengthToAngleLength(majorOuterRadius)
        val gapAngleMajorInner = separationLengthToAngleLength(majorInnerRadius)
        val gapAngleMinorOuter = separationLengthToAngleLength(minorOuterRadius)
        val gapAngleMinorInner = separationLengthToAngleLength(minorInnerRadius)



        for (i in 0 until totalSegments) {

            val pieceOffset = rotationOffset + i*pieceAngleLength
            var paint = if (i%3==0) axisPaintMajor else paintMajor

            drawSegment(canvas, centerX, centerY,
                majorInnerRadius, majorOuterRadius,
                startAngleInner = pieceOffset + gapAngleMajorInner, startAngleOuter = pieceOffset + gapAngleOuter,
                sweepAngleInner = pieceAngleLength - gapAngleMajorInner, sweepAngleOuter = pieceAngleLength - gapAngleOuter,
                paint)

            if(hasMinor) {
                paint = if (i % 3 == 0) axisPaintMinor else paintMinor
                drawSegment(
                    canvas,
                    centerX,
                    centerY,
                    minorInnerRadius,
                    minorOuterRadius,
                    startAngleInner = pieceOffset + gapAngleMinorInner,
                    startAngleOuter = pieceOffset + gapAngleMinorOuter,
                    sweepAngleInner = pieceAngleLength - gapAngleMinorInner,
                    sweepAngleOuter = pieceAngleLength - gapAngleMinorOuter,
                    paint
                )
            }
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

    fun pointInCircle(x:Float, y:Float, circleRadius: Float) :Boolean {
        val distX = x - centerX
        val distY = y - centerY
        val lenSquared = distY*distY+distX*distX
        val radiusSquared = circleRadius*circleRadius
        return lenSquared <= radiusSquared
    }

    fun isClickMinor(x: Float, y: Float) : Boolean {
        if(pointInCircle(x,y,minorInnerRadius))
            return false

        return pointInCircle(x,y, minorOuterRadius)
    }

    fun isClickMajor(x: Float, y:Float) : Boolean {
        if(pointInCircle(x,y,minorOuterRadius))
            return false
        return pointInCircle(x,y,majorOuterRadius)
    }

    fun getClickRadialIndex(x: Float, y:Float) : Int {
        val vectorX = x - centerX
        val vectorY = y - centerY

        //this returns the degree from the horizontal line to the vector (arctan (y/x)), values from -pi to pi
        //zero radians is at (1, 0), pi/2 is at (0,-1) (increases clockwise)
        val degreeFromVerticalRadians = atan2(vectorY,vectorX)

        //we need to make it so that the angle varies from 0 to 360
        var angle = toDegrees(degreeFromVerticalRadians.toDouble())
        if(angle<0) {
            angle += 360
        }

        //we have to rotate to account for the fact that zero is at rotationOffset
        //i have checked, the modulo is supposed to work as expected
        angle = (360 + angle - rotationOffset) % 360

        //we have to rotate everything 90 degrees to the left, because we want the distance from the vertical line
        angle = (360 + angle + 90) % 360


        val segmentSize = 360/12

        val index = floor(angle/segmentSize).toInt()

        return index
    }
}