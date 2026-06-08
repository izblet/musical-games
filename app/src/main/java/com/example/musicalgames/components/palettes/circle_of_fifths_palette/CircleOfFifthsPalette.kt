package com.example.musicalgames.components.palettes.circle_of_fifths_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Interval
import com.example.musicalgames.utils.Mode
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.NoteSpelling
import com.example.musicalgames.utils.SpellingPreference
import java.util.Locale

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

        fun noteName(chromaticNote: ChromaticNote, mode: Mode) : String {
            //TODO: for now this function does basically nothing
            val index : Int
            try {
                index = circlesMap[mode]!!.indexOf(chromaticNote)
            } catch(e : Exception) {
                Log.e("Circle of fifths", "note not found in the circle")
                return ""
            }
            val nameString = if(chromaticNote.isDiatonic()) NoteSpelling.spell(chromaticNote, SpellingPreference.SHARPS)
                else "${NoteSpelling.spell(chromaticNote, SpellingPreference.FLATS)}/${NoteSpelling.spell(chromaticNote, SpellingPreference.SHARPS)}"

            return when (mode) {
                Mode.AEOLIAN -> nameString.lowercase(Locale.getDefault())
                Mode.IONIAN -> nameString
                else -> "$nameString $mode"
            }
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