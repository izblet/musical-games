package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.utils.Interval
import kotlin.random.Random

class MentalView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val notes = listOf(
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    )

    private val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B")
    private val blackKeys = listOf("C#", "D#", "F#", "G#", "A#")

    private val paint = Paint().apply {
        textSize = 70f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private var messageText = ""
    private var selectedNote: String? = null
    private var questionNote: String = ""
    private var interval: String = ""
    private var correctAnswer: String = ""
    private var maxInterval: Int = 0
    private var disabled = true
    private var endListener : GameListener? = null
    var score = 0

    fun registerListener(listener: GameListener) {
        endListener = listener
    }

    fun setConstraint(max: Int) {
        maxInterval = max
    }

    fun generateQuestion() {
        disabled=false
        val startNoteIndex = Random.nextInt(notes.size)
        val semitoneInterval = Random.nextInt(1, maxInterval) // Interval between 1 and 12
        interval =  Interval.fromSemitones(semitoneInterval).name
        questionNote = notes[startNoteIndex]
        correctAnswer = notes[(startNoteIndex + semitoneInterval) % notes.size]

        messageText = "What is the note positioned at $interval from $questionNote?"
        invalidate() // Redraw to show the new question
    }

    private fun getWhiteKeyRect(index: Int): RectF {
        val widthPerWhiteKey = width / 7f
        val heightPerKey = height / 3f
        val padding = 5f
        return RectF(
            index * widthPerWhiteKey + padding,
            height - heightPerKey + padding,
            (index + 1) * widthPerWhiteKey - padding,
            height.toFloat() - padding
        )
    }

    private fun getBlackKeyRect(note: String): RectF? {
        val widthPerWhiteKey = width / 7f
        val heightPerKey = height / 3f
        val padding = 5f
        val xOffset = when (note) {
            "C#" -> 0.5f
            "D#" -> 1.5f
            "F#" -> 3.5f
            "G#" -> 4.5f
            "A#" -> 5.5f
            else -> return null
        }
        return RectF(
            xOffset * widthPerWhiteKey + padding,
            height - heightPerKey * 2 + padding,
            (xOffset + 1f) * widthPerWhiteKey - padding,
            height - heightPerKey - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val heightPerKey = height / 2f

        // Draw the question
        paint.color = Color.WHITE
        paint.textSize = 80f
        canvas.drawText(
            messageText,
            width / 2f, 150f, paint
        )

        // Draw the white keys
        for (i in whiteKeys.indices) {
            val note = whiteKeys[i]
            val rect = getWhiteKeyRect(i)
            paint.color = if (selectedNote == note) Color.LTGRAY else Color.WHITE
            paint.style = Paint.Style.FILL
            canvas.drawRect(rect, paint)
            // Draw black border
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            canvas.drawRect(rect, paint)
            paint.style = Paint.Style.FILL
            paint.color = Color.BLACK
            canvas.drawText(note, rect.centerX(), rect.bottom - heightPerKey / 4, paint)
        }

        // Draw the black keys
        for (i in blackKeys.indices) {
            val note = blackKeys[i]
            val rect = getBlackKeyRect(note)
            if (rect != null) {
                paint.color = if (selectedNote == note) Color.DKGRAY else Color.BLACK
                paint.style = Paint.Style.FILL
                canvas.drawRect(rect, paint)
                // Draw white border
                paint.color = Color.WHITE
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5f
                canvas.drawRect(rect, paint)
                paint.style = Paint.Style.FILL
                canvas.drawText(note, rect.centerX(), rect.bottom - heightPerKey / 4, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val widthPerWhiteKey = width / 7f
            val heightPerKey = height / 2f

            // Detect if a white key was pressed
            for (i in whiteKeys.indices) {
                val rect = getWhiteKeyRect(i)
                if (rect.contains(event.x, event.y)) {
                    selectedNote = whiteKeys[i]
                    checkAnswer(whiteKeys[i])
                    invalidate()
                    return true
                }
            }

            // Detect if a black key was pressed
            for (i in blackKeys.indices) {
                val note = blackKeys[i]
                val rect = getBlackKeyRect(note)
                if (rect != null && rect.contains(event.x, event.y)) {
                    selectedNote = blackKeys[i]
                    checkAnswer(blackKeys[i])
                    invalidate()
                    return true
                }
            }
        }
        return false
    }

    private fun checkAnswer(selected: String) {
        if (selected == correctAnswer) {
            // Correct answer, generate a new question
            disabled=true
            score++
            messageText = "Good!"
            handler.postDelayed({
                generateQuestion()
            }, 1000)
        } else {
            messageText = "The right answer is : $correctAnswer"
            handler.postDelayed({
                endListener?.onGameEnded()
            }, 1000)
        }
    }
}
