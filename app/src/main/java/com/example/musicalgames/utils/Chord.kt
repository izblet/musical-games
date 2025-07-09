package com.example.musicalgames.utils

import java.lang.StringBuilder

//this class should reflect how we think about chords semantically
//it should not be focused particularly on the chord notation, or the actual notes


//for now only one extension allowed, otherwise i'll go crazy
data class Chord (val root: ChromaticNote, val quality: Quality, val extension: Extension?) {
    init {
        require(qualityMatchesExtension(quality, extension))
    }

    companion object {
        enum class Quality {
            MAJOR, MINOR, DIMINISHED, AUGMENTED, HALF_DIMINISHED, DOMINANT
        }

        enum class Extension {
            MIN7, MAJ7, DIM7, MAJ6;
            fun getInterval():Interval {
                return when(this) {
                    MIN7->Interval.m7
                    MAJ7->Interval.M7
                    DIM7->Interval.M6
                    MAJ6->Interval.M6
                }
            }
        }

        enum class Alteration {
            B9, B13, SHARP9, SHARP11
        }

        val minSymb ="-"
        val augSymb="+"
        val dimSymb="o"
        val halfDimSymb="ø"
        val majSymb="∆"

        fun getSymbol(quality: Quality) : String {
            return when(quality) {
                Quality.MAJOR->""
                Quality.MINOR->minSymb
                Quality.DOMINANT-> ""
                Quality.AUGMENTED-> augSymb
                Quality.DIMINISHED-> dimSymb
                Quality.HALF_DIMINISHED-> halfDimSymb
            }
        }
        val validExtensions: Map<Quality, List<Extension?>> = Quality.entries.associateWith { quality ->
               when(quality) {
                   Quality.MAJOR -> listOf(null, Extension.MAJ7, Extension.MAJ6)
                   Quality.MINOR -> listOf(null, Extension.MIN7, Extension.MAJ7, Extension.MAJ6)
                   Quality.DOMINANT -> listOf(Extension.MIN7)
                   Quality.DIMINISHED -> listOf(null, Extension.DIM7)
                   Quality.AUGMENTED -> listOf(null, Extension.MAJ7)
                   Quality.HALF_DIMINISHED -> listOf(Extension.MIN7)
               }
        }
        fun qualityMatchesExtension(quality: Quality, extension: Extension?): Boolean {
            return  validExtensions[quality]!!.contains(extension)
        }
    }



    fun getChromaticNotes() : List<ChromaticNote> {
        val notes = mutableListOf(root)

        if(listOf(Quality.MAJOR, Quality.DOMINANT, Quality.AUGMENTED).contains(quality)) {
            notes.add(root.transpose(Interval.M3))
        } else {
            notes.add(root.transpose(Interval.m3))
        }

        if(listOf(Quality.MAJOR, Quality.MINOR, Quality.DOMINANT).contains(quality)) {
            notes.add(root.transpose(Interval.P5))
        } else if (quality == Quality.AUGMENTED) {
            notes.add(root.transpose(Interval.m6))
        } else {
            notes.add(root.transpose(Interval.TT))
        }

        if(extension!=null){
            notes.add(root.transpose(extension.getInterval()))
        }

        return notes
    }

    fun getName(sharpPreference: Boolean = true): String {
        val name = if(sharpPreference) StringBuilder(root.sharpPreferenceName()) else StringBuilder(root.flatPreferenceName())
        name.append(getSymbol(quality))
        if(extension==null)
            return name.toString()

        if(extension==Extension.MAJ7)
            return name.append(majSymb+"7").toString()

        if(extension==Extension.MAJ6)
            return name.append("6").toString()

        return name.append("7").toString()

    }

}