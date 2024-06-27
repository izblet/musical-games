package com.example.musicalgames.components.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.Note

class KeyboardView(context: Context, attrs: AttributeSet?) : View(context, attrs) {


    //just temporary values, because i don't want to waste time with null checks
    private val whiteKeyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blackKeyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blackKeyStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private val whiteKeyStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private var keysBitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private var keyWidth = 0
    private var keyHeight = 0
    private var min: Note = Note(0)
    private var max: Note = Note(0)
    private var keyNum: Int = 0

    private var listener: KeyboardListener? = null

    init {
        // Initialize paints for white and black keys
        whiteKeyPaint.color = Color.WHITE
        whiteKeyPaint.style = Paint.Style.FILL

        blackKeyPaint.color = Color.BLACK
        blackKeyPaint.style = Paint.Style.FILL

        whiteKeyStroke.color = Color.BLACK
        whiteKeyStroke.style = Paint.Style.STROKE

        blackKeyStroke.color = Color.WHITE
        blackKeyStroke.style = Paint.Style.STROKE
    }

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
            val left = i * keyWidth
            val top = 0
            val right = left + keyWidth
            val bottom = keyHeight

            val rect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            if(MusicUtil.isWhite(min.midiCode+i)) {
                canvas.drawRect(rect, whiteKeyPaint)
                canvas.drawRect(rect, whiteKeyStroke)
            }
            else {
                canvas.drawRect(rect, blackKeyPaint)
                canvas.drawRect(rect, blackKeyStroke)
            }

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

}
