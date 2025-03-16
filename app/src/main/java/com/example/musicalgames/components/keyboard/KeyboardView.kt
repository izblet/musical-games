package com.example.musicalgames.components.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.musicalgames.components.ComponentPaints.getBlackFillPaint
import com.example.musicalgames.components.ComponentPaints.getBlackStrokePaint
import com.example.musicalgames.components.ComponentPaints.getBlueFillPaint
import com.example.musicalgames.components.ComponentPaints.getDarkgrayFillPaint
import com.example.musicalgames.components.ComponentPaints.getLightgrayFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteStrokePaint
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.Note

class KeyboardView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    constructor(context: Context, grayedOut: Set<Int>, attrs: AttributeSet?) : this(context, attrs) {
        grayedOutSet.addAll(grayedOut)
    }
    //TODO: this should be divided into more classes using inheritance (too much functionality that is not used)

    private val whiteKeyPaint = getWhiteFillPaint(context)
    private val blackKeyPaint = getBlackFillPaint(context)
    private val darkgrayPaint = getDarkgrayFillPaint(context)
    private val lightgrayPaint = getLightgrayFillPaint(context)
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
    private val grayedOutSet : MutableSet<Int> = mutableSetOf()
    private var keyNum: Int = 0
    private var outlined = true
    private var clickDisabled = false

    private var listener: KeyboardListener? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Calculate dimensions for keys
        keyWidth = measuredWidth / (max.midiCode - min.midiCode + 1)
        keyHeight = measuredHeight

        if(keyHeight<=0)
            return

        setMeasuredDimension(measuredWidth, keyHeight)
        if (keysBitmap == null || keysBitmap?.width != measuredWidth || keysBitmap?.height != measuredHeight) {
            keysBitmap = Bitmap.createBitmap(measuredWidth, keyHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(keysBitmap!!)
            drawKeys(canvas!!)
        }
    }
    private fun redrawKey(index: Int, canvas: Canvas) {
        val left = index * keyWidth+padding
        val top = 0 + padding
        val right = left + keyWidth - padding
        val bottom = keyHeight - padding

        val currentKeyMidi = min.midiCode+index

        val rect = RectF(left, top, right, bottom)
        if(MusicUtil.isWhite(currentKeyMidi)) {
            if(currentKeyMidi in grayedOutSet) {
                canvas.drawRect(rect, lightgrayPaint)
            } else {
                canvas.drawRect(rect, whiteKeyPaint)
            }
            if(outlined)
                canvas.drawRect(rect, blackKeyStroke)
        }
        else {
            if(currentKeyMidi in grayedOutSet) {
                canvas.drawRect(rect, darkgrayPaint)
            } else {
                canvas.drawRect(rect, blackKeyPaint)
            }
            if(outlined)
                canvas.drawRect(rect, whiteKeyStroke)
        }
        if(colouredNote?.midiCode == min.midiCode+index)
            canvas.drawRect(rect, blueFillPaint)

    }

   private fun drawKeys(canvas: Canvas) {
        for (i in 0 until keyNum) {
            redrawKey(i, canvas)
        }
    }
    fun setOutlined(value: Boolean) {
        outlined = value
        if(canvas!=null) {
            drawKeys(canvas!!)
        }
    }
    fun setGrayedOut() {
        for (i in 0 until keyNum) {
            grayedOutSet.add(min.midiCode+i)
        }
    }
    fun setGrayedOutSet(set: Set<Int>) {
        grayedOutSet.clear()
        grayedOutSet.addAll(set)
        if(canvas!=null) {
            drawKeys(canvas!!)
        }
    }

    fun setGrayedOut(keyMidi: Int) {
        grayedOutSet.add(keyMidi)
        if(canvas!=null) {
            redrawKey(keyMidi-min.midiCode, canvas!!)
        }
    }
    fun unsetGrayedOut(keyMidi: Int) {
        grayedOutSet.remove(keyMidi)
        if(canvas != null) {
            redrawKey(keyMidi-min.midiCode, canvas!!)
        }
    }
    fun setDisabled(value: Boolean) {
        clickDisabled = value
    }
    fun isGrayedOut(keyMidi: Int) : Boolean {
        return (keyMidi in grayedOutSet)
    }
    fun getGrayedOut() : Set<Int> {
        return grayedOutSet
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
        if(clickDisabled)
            return false

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
