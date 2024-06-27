package com.example.musicalgames.utils

import kotlin.math.log2
import kotlin.math.round
import kotlin.math.roundToInt

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
    fun noteLetter(frequency: Double): String {
        // Calculate the octave of the given frequency
        val noteNum = round(12*log2(frequency/440.0)+49)
        val note = (noteNum+8).toInt()%12
        return noteToFrequencyMap.keys.elementAt(note)
       }
    fun isWhite(note: Note): Boolean {
        return isWhite(note.name)
    }
    fun midi(frequency: Double): Int {
        return (12 * (Math.log(frequency / 440.0) / Math.log(2.0)) + 69).roundToInt()
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
        return normalize(spice(note), min, max)
    }
    fun normalize(midicode: Int, min: Double, max:Double): Double {
        return normalize(spice(midicode), min, max)
    }
    fun normalize(spiceNote: Double, min: Double, max: Double):Double {
        return (spiceNote-min)/(max-min)
    }
    fun noteLetter(midicode: Int):String {
        return noteLetter(frequency(midicode))
    }
    fun noteLetter(note: String):String {
        return note.slice(0..0)
    }
    fun noteoctave(midicode: Int): Int {
        return midicode/12 -1
    }
    fun noteoctave(note: String): Int {
        val regex = Regex("\\d+")
        val matchResult = regex.find(note)
        return matchResult?.value?.toInt()!!
    }
    fun noteName(midicode: Int): String {
        return noteLetter(midicode) + noteoctave(midicode)
    }
    fun noteName(frequency: Double) : String {
        return noteName(midi(frequency))
    }

    fun isWhite(note: String) :Boolean {
        return !note.contains('#')
    }
    fun isWhite(midiNote: Int): Boolean {
        return isWhite(noteLetter(midiNote))
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
    fun cleffIndexC4(note: Note): Int {
        //returns the number of white notes from/to C4
        val letter = note.name.slice(0..0)
        val octaveNote = DiatonicNote.valueOf(letter).ordinal
        val octave = note.octave

        return octaveNote + octave*8 - 4*8
    }
    fun getScaleNotes(scale: Scale, root: ChromaticNote) : List<ChromaticNote> {
        val result: MutableList<ChromaticNote> = mutableListOf()
        val scaleDegrees = scale.getDegrees()

        for(degree in scaleDegrees)
            result.add(root.transpose(degree))

        return result
    }
    fun getScaleNotesFrom(scale: Scale, root: ChromaticNote, start: Note, num: Int) : List<Note> {
        val scaleNotes = getScaleNotes(scale, root)
        var octave = start.octave

        var i = scaleNotes.indexOf(start.noteChromatic)

        val result = mutableListOf<Note>()
        while(result.size < num) {
            result.add(Note(scaleNotes[i].toString()+octave))
            i=(i+1)%scaleNotes.size
            if(i==0)
                octave+=1
        }

        return result
    }
    fun getScaleNotesTo(scale: Scale, root: ChromaticNote, end: Note, num: Int) : List<Note> {
        val scaleNotes = getScaleNotes(scale, root)
        var i = scaleNotes.indexOf(end.noteChromatic)
        var octave = end.octave

        val result = mutableListOf<Note>()

        while(result.size < num) {
            result.add(Note(scaleNotes[i].toString()+octave))
            i=(i-1+scaleNotes.size)%scaleNotes.size
            if(i==scaleNotes.size-1)
                octave-=1
        }

        return result.reversed()
    }

    fun getWhiteKeysFrom(firstPitch: Int, num: Int) : List<Int> {
        val result = getScaleNotesFrom(Scale.MAJOR, ChromaticNote.C, Note(firstPitch), num)
        return result.map { note->note.midiCode }
    }
    fun getWhiteKeysTo(lastPitch: Int, num: Int) : List<Int> {
        val result = getScaleNotesTo(Scale.MAJOR, ChromaticNote.C, Note(lastPitch), num)
        return result.map{ note->note.midiCode}
    }
    fun getKeyIntervalFrom(pitch: String, num: Int): Int {
        return midi(pitch) +num
    }
}
