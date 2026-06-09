package com.example.musicalgames.music_model

// MusicUtils.kt
object MusicUtil {

    private fun parseOrThrow(note: String): Note =
        Note.parse(note) ?: throw IllegalArgumentException("Invalid note format $note")

    fun spice(note: String): Double {
        return spice(parseOrThrow(note).frequency)
    }
    fun spice(midiNote: Int): Double {
        return spice(Note(midiNote).frequency)
    }
    fun spiceNoteTopEnd(note:Int): Double {
        return (spice(note) + spice(note+1)) / 2
    }

    fun spiceNoteBottomEnd(note:Int): Double {
        return (spice(note) + spice(note-1)) / 2
    }
    fun normalize(midicode: Int, min: Double, max:Double): Double {
        return normalize(spice(midicode), min, max)
    }
    fun normalize(spiceNote: Double, min: Double, max: Double):Double {
        return (spiceNote-min)/(max-min)
    }

    fun isWhite(note: Note): Boolean {
        return note.noteChromatic.isDiatonic()
    }
    fun isWhite(midiNote: Int): Boolean {
        return isWhite(Note(midiNote))
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
        val chromatic = note.noteChromatic
        //sharp spelling always names a non-diatonic note after the white key below it
        //(C# names off C, D# off D, ...) - this mirrors the staff position the note is drawn at
        val letter = if (chromatic.isDiatonic())
            DiatonicNote.fromChromatic(chromatic)!!
        else
            DiatonicNote.fromChromatic(ChromaticNote.fromDegree(chromatic.ordinal - 1))!!
        val octave = note.octave

        return letter.ordinal + octave*8 - 4*8
    }
    fun getScaleNotes(scale: Scale, root: ChromaticNote) : List<ChromaticNote> {
        val result: MutableList<ChromaticNote> = mutableListOf()
        val scaleDegrees = scale.getDegrees()

        for(degree in scaleDegrees)
            result.add(root.transpose(degree))

        return result
    }
    fun getScaleNotesBetween(scale: Scale, root: ChromaticNote, min: Note, max: Note) : List<Note> {
        val scaleNotes = getScaleNotes(scale, root)
        val result = mutableListOf<Note>()

        for(midiCode in min.midiCode..max.midiCode) {
            val note = Note(midiCode)
            if(note.noteChromatic in scaleNotes)
                result.add(note)
        }

        return result
    }
    fun getScaleNotesFrom(scale: Scale, root: ChromaticNote, start: Note, num: Int) : List<Note> {
        val scaleNotes = getScaleNotes(scale, root)
        var octave = start.octave
        var newOctave = 0
        //this can be broken in exactly one place - the place will be the new octave (looking from below)
        if(scaleNotes[0]>scaleNotes[scaleNotes.size-1]) {
            newOctave=1
            while(true) {
                if(scaleNotes[newOctave-1]>scaleNotes[newOctave])
                    break
                newOctave++
            }

        }

        var i = scaleNotes.indexOf(start.noteChromatic)

        val result = mutableListOf<Note>()
        while(result.size < num) {
            result.add(Note(scaleNotes[i], octave))
            i=(i+1)%scaleNotes.size
            if(i==newOctave)
                octave+=1
        }

        return result
    }
    fun getScaleNotesTo(scale: Scale, root: ChromaticNote, end: Note, num: Int) : List<Note> {
        val scaleNotes = getScaleNotes(scale, root)
        var i = scaleNotes.indexOf(end.noteChromatic)
        var octave = end.octave

        var newOctave = scaleNotes.size-1
        if(scaleNotes[0]>scaleNotes[scaleNotes.size-1]) {
            newOctave = scaleNotes.size-2
            while(true) {
                if(scaleNotes[newOctave]>scaleNotes[newOctave+1])
                    break
                newOctave--
            }

        }

        val result = mutableListOf<Note>()

        while(result.size < num) {
            result.add(Note(scaleNotes[i], octave))
            i=(i-1+scaleNotes.size)%scaleNotes.size
            if(i==newOctave)
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
    fun addInterval(note: Note, interval: Interval) : Note {
        return Note(note.midiCode + interval.getSemitones())
    }
    fun addInterval(note: ChromaticNote, interval: Interval) : ChromaticNote {
        return addInterval(Note(note, Octave.o1), interval).noteChromatic
    }
}
