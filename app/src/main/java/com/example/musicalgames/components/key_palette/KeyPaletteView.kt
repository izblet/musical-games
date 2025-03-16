package com.example.musicalgames.components.key_palette

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.components.ComponentPaints.getBlackFillPaint
import com.example.musicalgames.components.ComponentPaints.getBlackStrokePaint
import com.example.musicalgames.components.ComponentPaints.getBlackTextPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteStrokePaint
import com.example.musicalgames.components.ComponentPaints.getWhiteTextPaint
import com.example.musicalgames.utils.DiatonicNote
import kotlin.math.floor

class KeyPaletteView(context: Context) : View(context) {
    private var listener: KeyPaletteListener? = null

    private val whiteStrokePaint = getWhiteStrokePaint(context)
    private val blackStrokePaint = getBlackStrokePaint(context)
    private val blackFillPaint = getBlackFillPaint(context)
    private val whiteFillPaint = getWhiteFillPaint(context)
    private val whiteTextPaint = getWhiteTextPaint(context)
    private val blackTextPaint = getBlackTextPaint(context)

    private val padding = whiteStrokePaint.strokeWidth
    private var keyWidth = 0
    private var keyHeight:Float = 0f
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var height = 0


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        keyWidth = measuredWidth / DiatonicNote.valuesSize()
        keyHeight = (measuredHeight/2f)
        height = measuredHeight


        setMeasuredDimension(measuredWidth, measuredHeight)
        if (bitmap == null || bitmap?.width != measuredWidth || bitmap?.height != measuredHeight) {
            bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)
            drawKeys(canvas!!)
        }
    }

    private fun drawKeys(canvas: Canvas) {
        for (i in 0..<DiatonicNote.valuesSize()) {
            val note = DiatonicNote.fromDegree(i)
            val rect = getWhiteKeyRect(i)
            canvas.drawRect(rect, whiteFillPaint)
            canvas.drawText(note.name, rect.centerX(), rect.bottom - keyHeight / 4, blackTextPaint)
            canvas.drawRect(rect, blackStrokePaint)
        }

        for (i in 0..<DiatonicNote.valuesSize()-1) {
            //we will check if we want to draw a note on the right of i
            //we do not check B
            val diatonicBelow = DiatonicNote.fromDegree(i)
            val note = diatonicBelow.chromaticAbove()
            if (note.isDiatonic())
                continue

            val rect = getBlackKeyAboveRect(DiatonicNote.fromDegree(i))
            canvas.drawRect(rect, blackFillPaint)
            canvas.drawText(
                note.toString(),
                rect.centerX(),
                rect.bottom - keyHeight / 4,
                whiteTextPaint
            )
            canvas.drawRect(rect, whiteStrokePaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    fun registerListener(listener: KeyPaletteListener) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.y > keyHeight) {
                    //we have pressed a white key
                    val keyIndex = floor(event.x / keyWidth).toInt()
                    listener?.onClicked(DiatonicNote.fromDegree(keyIndex).chromaticNote)
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
                            listener?.onClicked(note)
                    }
                }
            }
        }
        return true
    }

    private fun getWhiteKeyRect(index: Int): RectF {
        return RectF(
            index * keyWidth + padding,
            height - keyHeight + padding,
            (index + 1) * keyWidth - padding,
            height.toFloat() - padding
        )
    }

    private fun getBlackKeyAboveRect(noteBelow: DiatonicNote): RectF {
        val i = noteBelow.ordinal
        val xOffset = i + 0.5f

        return RectF(
            xOffset * keyWidth + padding,
            height - keyHeight * 2 + padding,
            (xOffset + 1f) * keyWidth - padding,
            height - keyHeight - padding
        )
    }
}