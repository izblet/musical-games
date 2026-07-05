package com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class CircleOfFifthsPalette(context: Context, attr: AttributeSet?) : CircleOfFifthsPaletteView(
    context,
    attr
) {
    private var listener : CirclePaletteListener? = null

    fun registerListener(listener: CirclePaletteListener) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null)
            return false

        //TODO: this should be removed as well
        if(isClickCircle(event.x, event.y)) {
            val index = getClickRadialIndex(event.x, event.y)
            listener?.onKeyClicked(index)

        } else {
            Log.d("circle", "point not in the circle")
            return false
        }


        return super.onTouchEvent(event)
    }
}