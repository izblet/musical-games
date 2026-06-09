package com.example.musicalgames.components.palettes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.components.ComponentPaints.getBlackFillPaint
import com.example.musicalgames.components.ComponentPaints.getBlackTextPaint
import com.example.musicalgames.components.ComponentPaints.getDarkgrayFillPaint
import com.example.musicalgames.components.ComponentPaints.getLightgrayFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteTextPaint
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.DiatonicNote
import kotlin.math.floor

abstract class KeyboardBasedPalette @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : View(context, attributeSet, defStyle) {
    //private val whiteStrokePaint = getWhiteStrokePaint(context)
    //private val blackStrokePaint = getBlackStrokePaint(context)
    private val fillBlackActive = getBlackFillPaint(context)
    private val fillWhiteActive = getWhiteFillPaint(context)
    private val textWhiteActive = getWhiteTextPaint(context)
    private val textBlackActive = getBlackTextPaint(context)

    private val fillBlackInactive = getDarkgrayFillPaint(context)
    private val fillWhiteInactive = getLightgrayFillPaint(context)
    private val textWhiteInactive = getWhiteTextPaint(context)
    private val textBlackInactive = getBlackTextPaint(context)

    private val grayedOutSet : MutableSet<ChromaticNote> = mutableSetOf()

    private val margins = 4f
    private var keyWidth = 0f
    private var keyHeight:Float = 0f
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var height = 0


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        keyWidth = measuredWidth / (DiatonicNote.valuesSize().toFloat())
        keyHeight = (measuredHeight/2f)
        height = measuredHeight
        setMeasuredDimension(measuredWidth, measuredHeight)

        if(keyHeight ==0f || keyWidth == 0f) {
            return
        }

        if (bitmap == null || bitmap?.width != measuredWidth || bitmap?.height != measuredHeight) {
            bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)
            drawKeys(canvas!!)
        }
    }
    abstract fun keyLabel(note: ChromaticNote) : String

    private fun Canvas.drawMultilineText(text: String, x: Float, y: Float, paint: Paint) {
        val lines = text.split("\n")
        var currentY = y
        for (line in lines) {
            drawText(line, x, currentY, paint)
            currentY += paint.fontSpacing
        }
    }

    private fun drawKeys(canvas: Canvas) {

        //First we draw all diatonic notes
        for (i in 0..<DiatonicNote.valuesSize()) {
            val note = DiatonicNote.fromDegree(i)
            val rect = getWhiteKeyRect(i)
            if(grayedOutSet.contains(note.chromaticNote)) {
                canvas.drawRect(rect, fillWhiteInactive)
            } else {
                canvas.drawRect(rect, fillWhiteActive)
            }
            canvas.drawText(keyLabel(note.chromaticNote), rect.centerX(), rect.bottom - keyHeight / 4, textBlackActive)
            //canvas.drawRect(rect, blackStrokePaint)
        }

        //Then we draw all chromatic notes
        for (i in 0..<DiatonicNote.valuesSize()-1) {
            //we will check if we want to draw a note on the right of i
            //we do not check B
            val diatonicBelow = DiatonicNote.fromDegree(i)
            val note = diatonicBelow.chromaticAbove()
            if (note.isDiatonic())
                continue

            val rect = getBlackKeyAboveRect(DiatonicNote.fromDegree(i))
            if(note in grayedOutSet) {
                canvas.drawRect(rect, fillBlackInactive)
            } else {
                canvas.drawRect(rect, fillBlackActive)
            }

            val keyCenter = rect.bottom - keyHeight/2
            canvas.drawMultilineText(
                keyLabel(note),
                rect.centerX(),
                keyCenter,
                textWhiteActive
            )
            //canvas.drawRect(rect, whiteStrokePaint)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }
    private fun getWhiteKeyRect(index: Int): RectF {
        return RectF(
            index * keyWidth + margins,
            height - keyHeight*2 + margins,
            (index + 1) * keyWidth - margins,
            height.toFloat() - margins
        )
    }

    private fun getBlackKeyAboveRect(noteBelow: DiatonicNote): RectF {
        val i = noteBelow.ordinal
        val xOffset = i + 0.5f

        return RectF(
            xOffset * keyWidth + margins,
            height - keyHeight * 2 + margins,
            (xOffset + 1f) * keyWidth - margins,
            height - keyHeight - margins
        )
    }
    fun setGrayedOut() {
        for (note in ChromaticNote.entries) {
            grayedOutSet.add(note)
        }
        if(canvas!=null) {
            drawKeys(canvas!!)
        }
    }
    fun setGrayedOutSet(set: Set<ChromaticNote>) {
        grayedOutSet.clear()
        grayedOutSet.addAll(set)
        if(canvas!=null) {
            drawKeys(canvas!!)
        }
    }
    fun setGrayedOut(note: ChromaticNote) {
        grayedOutSet.add(note)
        if(canvas!=null) {
            drawKeys(canvas!!)
        }
    }
    fun unsetGrayedOut(note: ChromaticNote) {
        grayedOutSet.remove(note)
        if(canvas!=null) {
            drawKeys(canvas!!)
        }
    }
    fun getGrayedOut() : Set<ChromaticNote> {
        return grayedOutSet
    }
    fun isGrayedOut(note : ChromaticNote) : Boolean {
        return (grayedOutSet.contains(note))
    }

    abstract fun onClickAction(note : ChromaticNote)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.y > keyHeight) {
                    //we have pressed a white key
                    val keyIndex = floor(event.x / keyWidth).toInt()
                    onClickAction(DiatonicNote.fromDegree(keyIndex).chromaticNote)
                } else {
                    //we have pressed a black key
                    val iDiatonicBelow = floor((event.x - (keyWidth / 2f)) / keyWidth).toInt()
                    if(iDiatonicBelow<0 || iDiatonicBelow == DiatonicNote.B.ordinal)
                        return true
                    val diatonicBelow = DiatonicNote.fromDegree(iDiatonicBelow)
                    val note = diatonicBelow.chromaticAbove()
                    if (!note.isDiatonic()) {
                        val rect = getBlackKeyAboveRect(diatonicBelow)

                        if (rect.contains(event.x, event.y))
                            onClickAction(note)
                    }
                }
            }
        }
        return true
    }
}