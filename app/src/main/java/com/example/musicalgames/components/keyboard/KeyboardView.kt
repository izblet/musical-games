package com.example.musicalgames.components.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.components.Paints.getBlackFillPaint
import com.example.musicalgames.components.Paints.getBlackStrokePaint
import com.example.musicalgames.components.Paints.getBlueFillPaint
import com.example.musicalgames.components.Paints.getWhiteFillPaint
import com.example.musicalgames.components.Paints.getWhiteStrokePaint
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.Note

class KeyboardView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val whiteKeyPaint = getWhiteFillPaint(context)
    private val blackKeyPaint = getBlackFillPaint(context)
    private val blackKeyStroke = getBlackStrokePaint(context)
    private val whiteKeyStroke = getWhiteStrokePaint(context)
    private val blueFillPaint = getBlueFillPaint(context)
    private val padding = whiteKeyStroke.strokeWidth

    private var keysBitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private var keyWidth = 0
    private var keyHeight = 0
    private var min: Note = Note(0)
    private var max: Note = Note(0)
    private var colouredNote: Note? = null
    private var keyNum: Int = 0

    private var listener: KeyboardListener? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Calculate dimensions for keys
        keyWidth = measuredWidth / (max.midiCode - min.midiCode + 1)
        keyHeight = measuredHeight

        setMeasuredDimension(measuredWidth, keyHeight)
        if (keysBitmap == null || keysBitmap?.width != measuredWidth || keysBitmap?.height != measuredHeight) {
            keysBitmap = Bitmap.createBitmap(measuredWidth, keyHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(keysBitmap!!)
            drawKeys(canvas!!)
        }
    }


   private fun drawKeys(canvas: Canvas) {
        // Draw white keys
        for (i in 0 until keyNum) {
            val left = i * keyWidth+padding
            val top = 0 + padding
            val right = left + keyWidth - padding
            val bottom = keyHeight - padding

            val rect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            if(MusicUtil.isWhite(min.midiCode+i)) {
                canvas.drawRect(rect, whiteKeyPaint)
                canvas.drawRect(rect, blackKeyStroke)
            }
            else {
                canvas.drawRect(rect, blackKeyPaint)
                canvas.drawRect(rect, whiteKeyStroke)
            }
            if(colouredNote?.midiCode == min.midiCode+i)
                canvas.drawRect(rect, blueFillPaint)

        }
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        keysBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }
    fun registerListener(listener: KeyboardListener) {
        this.listener=listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchedKeyId = min.midiCode + (event.x / keyWidth).toInt()
                listener?.onKeyClicked(Note(touchedKeyId))
                invalidate()
            }
        }
        return true
    }

    fun setRange(min: Note, max: Note) {
        this.max=max
        this.min=min
        keyNum=max.midiCode-min.midiCode+1
        requestLayout()
    }
    fun setColoured(key: Note) {
        colouredNote = key
    }

}
