package com.example.musicalgames.components.palettes.circle_of_fifths_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import com.example.musicalgames.utils.MusicUtil
import java.util.Locale

class CircleOfFifthsPalette(context: Context, attr: AttributeSet?) : CircleOfFifthsPaletteView(
    context,
    attr
) {
    //you should actually make a circle of fifths class in the same way that you have notes, intervals, etc
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
        fun noteName(chromaticNote: ChromaticNote, major: Boolean) : String {
            if(major) {
                val index = majorList.indexOf(chromaticNote)
                return if(chromaticNote.isDiatonic())
                    chromaticNote.toString()
                else if (index>=8) {
                    chromaticNote.flatPreferenceName()
                } else {
                    "${chromaticNote.flatPreferenceName()}/${chromaticNote.sharpPreferenceName()}"
                }
            }


            val index = minorList.indexOf(chromaticNote)
            val noteName: String
           if(chromaticNote.isDiatonic()) {
               noteName = chromaticNote.toString()
           } else if( index<=5){
               noteName = chromaticNote.sharpPreferenceName()
           } else if(index == 6) {
               noteName = "${chromaticNote.flatPreferenceName()}/${chromaticNote.sharpPreferenceName()}"
           } else {
               noteName = chromaticNote.flatPreferenceName()
           }

           return noteName.lowercase(Locale.getDefault())
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

        } else if(isClickMinor(event.x, event.y)&&hasMinor()) {
            val index = getClickRadialIndex(event.x, event.y)
            listener?.onKeyClicked(minorList[index], major= false)
        } else {
            Log.d("circle", "point not in the circle")
            return false
        }


        return super.onTouchEvent(event)
    }
}