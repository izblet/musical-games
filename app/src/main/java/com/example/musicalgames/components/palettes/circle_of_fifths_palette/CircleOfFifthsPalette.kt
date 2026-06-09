package com.example.musicalgames.components.palettes.circle_of_fifths_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.DiatonicNote
import com.example.musicalgames.music_model.Interval
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.music_model.MusicUtil

class CircleOfFifthsPalette(context: Context, attr: AttributeSet?) : CircleOfFifthsPaletteView(
    context,
    attr
) {
    //TODO: wrong direction of dependency, you should actually make a circle of fifths class in the same way that you have notes, intervals, etc and the view should just use that class

    private var listener : CirclePaletteListener? = null
    companion object {
        private val circlesMap = generateCircles()

        private fun generateCircle(firstNote: ChromaticNote) : List<ChromaticNote> {
            val list : MutableList<ChromaticNote> = mutableListOf()
            var note = firstNote
            for(i in 0..<12) {
                list.add(note)
                note = MusicUtil.addInterval(note, Interval.P5)
            }
            return list
        }
        private fun generateCircles() : HashMap<Mode, List<ChromaticNote>> {
            val map = HashMap<Mode, List<ChromaticNote>>()
            for(note in DiatonicNote.entries) {
                map[Mode.fromDiatonicNote(note)]= generateCircle(note.chromaticNote)
            }
            return map
        }

        fun noteAtIndex(index :Int, mode : Mode) : ChromaticNote {
            return circlesMap[mode]!![index]
        }
        fun noteIndex(note : ChromaticNote, mode: Mode) : Int {
            return circlesMap[mode]!!.indexOf(note)
        }
    }

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