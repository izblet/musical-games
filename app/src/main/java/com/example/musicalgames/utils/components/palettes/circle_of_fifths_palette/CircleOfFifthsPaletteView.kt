package com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class CircleOfFifthsPaletteView(context: Context, attr: AttributeSet?, model: CircleOfFifthsPaletteModel) : CircleOfFifthsView(
    context,
    attr,
    model
) {
    //clickable circle of fifths
    var onKeyClicked: ((index: Int) -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null)
            return false

        if(isClickCircle(event.x, event.y)) {
            val index = getClickRadialIndex(event.x, event.y)
            onKeyClicked?.invoke(index)

        } else {
            Log.d("circle", "point not in the circle")
            return false
        }


        return super.onTouchEvent(event)
    }
}
