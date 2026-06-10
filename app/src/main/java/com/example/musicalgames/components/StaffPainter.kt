package com.example.musicalgames.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import com.example.musicalgames.music_model.Accidental
import com.example.musicalgames.music_model.KeySignature
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.DiatonicNote
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.display.SpellingPreference


class StaffPainter(private val clefBitmap: Bitmap, private val sharpBitmap: Bitmap, private val flatBitmap: Bitmap, color: Int = Color.BLACK) {
    companion object {
        //key-signature accidental positions as clefIndex (diatonic steps from C4), in the
        //order KeySignature.sharpOrder/flatOrder lists them. Treble clef positions are the
        //standard engraving positions; bass clef positions are
        //shifted by BASS_CLEF_OFFSET, the difference between getNoteYPosition's two
        //middleCPosition formulas, so each accidental lands on the same staff line/space
        private val sharpPositions = listOf(10, 7, 11, 8, 5, 9, 6)
        private val flatPositions = listOf(6, 9, 5, 8, 4, 7, 3)
        private const val BASS_CLEF_OFFSET = -13
    }

    private val staffLinePaint = Paint().apply {
        this.color = color
        strokeWidth = 5f
    }

    private val notePaint = Paint().apply {
        this.color = color
    }

    private val clefPaint = Paint().apply {
        colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
    private var top:Float = 0f
    private var bottom:Float = 0f
    private var noteRadius = 0f
    private var lineSpacing = 0f
    private var height = 0f
    private var treble = true
    private var keySignature = KeySignature(0)

    fun setConstraints(top: Float, bottom: Float) {
        this.top=top
        this.bottom=bottom
        height=(bottom-top)
        noteRadius=lineSpacing/2
        this.lineSpacing = (height)/4
    }

    fun setTreble(treble: Boolean) {
        this.treble = treble
    }

    fun setKeySignature(keySignature: KeySignature) {
        this.keySignature = keySignature
    }

    fun setStaffParameters(treble: Boolean, keySignature: KeySignature) {
        setTreble(treble)
        setKeySignature(keySignature)
    }

    private fun getNoteYPosition(clefIndex: Int): Float {
        val middleCPosition : Float =
            if(treble) top + 5 * lineSpacing
            else top - 3*lineSpacing/2

        return middleCPosition - clefIndex * (lineSpacing / 2)
    }

    fun drawStaff(canvas: Canvas, left: Float, width: Float, midiNotes: List<Int>) {
        drawStaffLines(canvas, left, width)
        val clefWidth = drawClef(canvas, left)
        val keySignatureWidth = drawKeySignature(canvas, left + clefWidth + lineSpacing * 0.3f)
        val notesLeft = if (keySignatureWidth > 0f) left + 150f + keySignatureWidth + lineSpacing * 0.3f else left + 150f
        drawNotes(canvas, notesLeft, midiNotes)
    }

    private fun drawStaffLines(canvas: Canvas, left: Float, width: Float) {
        val startX = left
        val stopX = left + width
        for (i in 0..4) {
            val y = top + i * lineSpacing
            canvas.drawLine(startX, y, stopX, y, staffLinePaint)
        }
    }

    private fun drawClef(canvas: Canvas, left: Float): Float {
        val trebleClefWidth = height / 2
        val trebleClefHeight = clefBitmap.height * (trebleClefWidth / clefBitmap.width)
        val dstRect = RectF(left, top, left + trebleClefWidth, top + trebleClefHeight)
        canvas.drawBitmap(clefBitmap, null, dstRect, clefPaint)
        return trebleClefWidth
    }

    private fun drawKeySignature(canvas: Canvas, left: Float): Float {
        val accidentalCount = keySignature.notes.size
        if (accidentalCount == 0) return 0f

        val bitmap = if (keySignature.accidental == Accidental.SHARP) sharpBitmap else flatBitmap
        val positions = if (keySignature.accidental == Accidental.SHARP) sharpPositions else flatPositions
        val clefOffset = if (treble) 0 else BASS_CLEF_OFFSET

        val accidentalWidth = lineSpacing
        val accidentalHeight = bitmap.height * (accidentalWidth / bitmap.width)
        val spacing = accidentalWidth * 0.9f

        for (i in 0 until accidentalCount) {
            val centerY = getNoteYPosition(positions[i] + clefOffset)
            val x = left + i * spacing
            val dstRect = RectF(x, centerY - accidentalHeight / 2, x + accidentalWidth, centerY + accidentalHeight / 2)
            canvas.drawBitmap(bitmap, null, dstRect, clefPaint)
        }

        return accidentalCount * spacing
    }

    private fun drawNotes(canvas: Canvas, left: Float, midiNotes: List<Int>) {
        val noteSpacing = 200f
        midiNotes.forEachIndexed { index, midiNote ->
            val clefIndex = cleffIndexC4(Note(midiNote))
            val y = getNoteYPosition(clefIndex)
            drawLedgerLinesIfNeeded(canvas, left + index * noteSpacing, y)
            canvas.drawCircle(left + index * noteSpacing, y, noteRadius, notePaint)
        }
    }

    private fun drawLedgerLinesIfNeeded(canvas: Canvas, x: Float, y: Float) {
        val bottomStaffLineY = top + 4 * lineSpacing
        val topStaffLineY = top
        val ledgerLineLength = 40f

        if (y > bottomStaffLineY + lineSpacing/2) {
            var currentY = bottomStaffLineY + lineSpacing
            while (currentY <= y) {
                canvas.drawLine(x - ledgerLineLength, currentY, x + ledgerLineLength, currentY, staffLinePaint)
                currentY += lineSpacing
            }
        } else if (y < topStaffLineY - lineSpacing/2) {
            var currentY = topStaffLineY - lineSpacing
            while (currentY >= y ) {
                canvas.drawLine(x - ledgerLineLength, currentY, x + ledgerLineLength, currentY, staffLinePaint)
                currentY -= lineSpacing
            }
        }
    }
    fun cleffIndexC4(note: Note, sharpPreference: Boolean = true): Int {
        //returns the number of white notes from/to C4
        val chromatic = note.noteChromatic
        val chromaticOffset : Int = if(sharpPreference) -1 else 1

        //sharp spelling always names a non-diatonic note after the white key below it
        //(C# names off C, D# off D, ...) - this mirrors the staff position the note is drawn at
        val letter = if (chromatic.isDiatonic())
            DiatonicNote.fromChromatic(chromatic)!!
        else
            DiatonicNote.fromChromatic(ChromaticNote.fromDegree(chromatic.ordinal + chromaticOffset))!!
        val octave = note.octave

        return letter.ordinal + octave*8 - 4*8
    }
}

