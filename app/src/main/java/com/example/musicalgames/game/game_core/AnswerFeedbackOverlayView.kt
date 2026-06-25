package com.example.musicalgames.game_activity

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.example.musicalgames.R
import kotlin.math.hypot

//sits on top of the game container and briefly tints the screen edges on a right/wrong answer -
//see ScreenHighlighter, which is what actually triggers flashCorrect()/flashWrong()
class AnswerFeedbackOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()
    private var currentColor: Int = 0
    private var animator: ObjectAnimator? = null

    init {
        isClickable = false
        isFocusable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        alpha = 0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && currentColor != 0) {
            rebuildShader(w, h)
        }
    }

    fun flashCorrect() = flash(resolveColor(R.attr.rightAnswerColor))
    fun flashWrong() = flash(resolveColor(R.attr.wrongAnswerColor))

    private fun flash(color: Int) {
        currentColor = color
        if (width > 0 && height > 0) {
            rebuildShader(width, height)
        }

        animator?.cancel()
        //pop to full intensity immediately (an animator starting from the current, already-faded
        //alpha doesn't reliably trigger a redraw on the very first frame), then fade out smoothly
        alpha = 1f
        invalidate()
        animator = ObjectAnimator.ofFloat(this, ALPHA, 1f, 0f).apply {
            duration = ScreenHighlighter.FLASH_DURATION_MS
            //the theme colours are more muted than the bright test colours used earlier, so a
            //linear fade reads as "vanishing immediately" against the dark background - holding
            //near full intensity for most of the duration and only dropping off at the very end
            //keeps it visible for the whole duration instead of just the start
            interpolator = AccelerateInterpolator(2f)
            start()
        }
    }

    private fun resolveColor(attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun rebuildShader(w: Int, h: Int) {
        val cx = w / 2f
        val cy = h / 2f
        //transparent at the center, full colour past the radius - a wide radius keeps most of
        //the screen clear and concentrates colour near the edges/corners, like a border glow
        val radius = hypot(cx, cy) * RADIUS_MULTIPLIER
        paint.shader = RadialGradient(
            cx, cy, radius,
            currentColor and 0x00FFFFFF,
            currentColor,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (paint.shader != null) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }

    companion object {
        private const val RADIUS_MULTIPLIER = 2.5f
    }
}
