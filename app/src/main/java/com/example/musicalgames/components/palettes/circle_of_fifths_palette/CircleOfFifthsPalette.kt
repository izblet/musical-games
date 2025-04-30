package com.example.musicalgames.components.palettes.circle_of_fifths_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import com.example.musicalgames.utils.MusicUtil

class CircleOfFifthsPalette(context: Context, attr: AttributeSet) : CircleOfFifthsPaletteView(context, attr) {
    private var listener : CirclePaletteListener? = null
    companion object {
        val majorList = generateCircle(ChromaticNote.C)
        val minorList = generateCircle(ChromaticNote.A)

        private fun generateCircle(firstNote: ChromaticNote) : List<ChromaticNote> {
            val list : MutableList<ChromaticNote> = mutableListOf()
            var note = firstNote
            for(i in 0..<12) {
                list.add(note)
                note = MusicUtil.addInterval(note, Interval.P5)
            }
            return list
        }
    }

    fun registerListener(listener: CirclePaletteListener) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null)
            return false

        if(isClickMajor(event.x, event.y)) {
            val index = getClickRadialIndex(event.x, event.y)
            listener?.onKeyClicked(majorList[index], major= true)

        } else if(isClickMinor(event.x, event.y)) {
            val index = getClickRadialIndex(event.x, event.y)
            listener?.onKeyClicked(minorList[index], major= false)
        } else {
            Log.d("circle", "point not in the circle")
            return false
        }


        return super.onTouchEvent(event)
    }
}