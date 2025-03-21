package com.example.musicalgames.components.palettes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.components.ComponentPaints.getBlackFillPaint
import com.example.musicalgames.components.ComponentPaints.getBlackTextPaint
import com.example.musicalgames.components.ComponentPaints.getDarkgrayFillPaint
import com.example.musicalgames.components.ComponentPaints.getLightgrayFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteTextPaint
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote
import kotlin.math.floor

abstract class KeyboardBasedPalette @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : View(context, attributeSet, defStyle) {
    //private val whiteStrokePaint = getWhiteStrokePaint(context)
    //private val blackStrokePaint = getBlackStrokePaint(context)
    private val blackFillPaint = getBlackFillPaint(context)
    private val whiteFillPaint = getWhiteFillPaint(context)
    private val whiteTextPaint = getWhiteTextPaint(context)
    private val blackTextPaint = getBlackTextPaint(context)
    private val darkgrayPaint = getDarkgrayFillPaint(context)
    private val lightgrayPaint = getLightgrayFillPaint(context)

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

    private fun drawKeys(canvas: Canvas) {
        Log.d("keyboard", "redraw")

        //First we draw all diatonic notes
        for (i in 0..<DiatonicNote.valuesSize()) {
            val note = DiatonicNote.fromDegree(i)
            val rect = getWhiteKeyRect(i)
            if(grayedOutSet.contains(note.chromaticNote)) {
                canvas.drawRect(rect, lightgrayPaint)
            } else {
                Log.d("keyboard","white note")
                canvas.drawRect(rect, whiteFillPaint)
            }
            canvas.drawText(keyLabel(note.chromaticNote), rect.centerX(), rect.bottom - keyHeight / 4, blackTextPaint)
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
                canvas.drawRect(rect, darkgrayPaint)
            } else {
                canvas.drawRect(rect, blackFillPaint)
            }

            canvas.drawText(
                keyLabel(note),
                rect.centerX(),
                rect.bottom - keyHeight / 4,
                whiteTextPaint
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
        Log.d("keyboard", "unset gray")
        grayedOutSet.remove(note)
        if(canvas!=null) {
            Log.d("keyboard","canvas is not null")
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