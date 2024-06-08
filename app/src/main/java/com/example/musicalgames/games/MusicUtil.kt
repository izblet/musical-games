package com.example.musicalgames.games

import kotlin.math.log2
import kotlin.math.round

// MusicUtils.kt
object MusicUtil {
    private val noteToFrequencyMap = mapOf(
        "C" to 261.63, "C#" to 277.18, "D" to 293.66, "D#" to 311.13,
        "E" to 329.63, "F" to 349.23, "F#" to 369.99, "G" to 392.00,
        "G#" to 415.30, "A" to 440.00, "A#" to 466.16, "B" to 493.88
    )

    @Throws
    fun frequency(note: String): Double {
        val exception = java.lang.IllegalArgumentException("Invalid note format $note")
        val noteRegex = """([A-Ga-g]#?)(\d+)""".toRegex()
        val matchResult = noteRegex.matchEntire(note) ?: throw exception
        val (noteName, octave) = matchResult.destructured
        val baseFrequency = noteToFrequencyMap[noteName.toUpperCase()] ?: throw exception
        val frequency = baseFrequency * Math.pow(2.0, (octave.toInt() - 4).toDouble())
        return frequency
    }

    @Throws(IllegalArgumentException::class)
    fun notename(frequency: Double): String {
        // Calculate the octave of the given frequency
        val noteNum = round(12*log2(frequency/440.0)+49)
        val note = (noteNum+8).toInt()%12
        return noteToFrequencyMap.keys.elementAt(note)
       }
    fun isWhite(note: Note): Boolean {
        return isWhite(note.name)
    }
    fun midi(frequency: Double): Int {
        return (12 * (Math.log(frequency / 440.0) / Math.log(2.0)) + 69).toInt()
    }
    fun midi(note: String) : Int {
        return midi(frequency(note))
    }

    fun frequency(midiCode: Int): Double {
        return 440.0 * Math.pow(2.0, (midiCode - 69) / 12.0)
    }
    fun spice(note: String): Double {
        return spice(frequency(note))
    }
    fun spice(midiNote: Int): Double {
        return spice(frequency(midiNote))
    }
    fun spiceNoteTopEnd(note:Int): Double {
        return (spice(note) + spice(note+1)) / 2
    }

    fun spiceNoteBottomEnd(note:Int): Double {
        return (spice(note) + spice(note-1)) / 2
    }
    fun normalize(note: String, min:Double, max:Double) : Double {
        return (spice(note)-min)/(max-min)
    }
    fun normalize(midicode: Int, min: Double, max:Double): Double {
        return (spice(midicode)-min)/(max-min)
    }
    fun notename(midicode: Int):String {
        return notename(frequency(midicode))
    }

    fun isWhite(note: String) :Boolean {
        return !note.contains('#')
    }
    fun isWhite(midiNote: Int): Boolean {
        return isWhite(notename(midiNote))
    }

    fun spice(hz: Double): Double {
        //TODO: move to resources
        val PT_OFFSET = 25.58
        val PT_SLOPE = 63.07
        val FMIN = 10.0
        val BINS_PER_OCTAVE = 12.0

        val cqtBin = BINS_PER_OCTAVE * (Math.log(hz / FMIN) / Math.log(2.0)) - PT_OFFSET
        return (cqtBin / PT_SLOPE)
    }
}
