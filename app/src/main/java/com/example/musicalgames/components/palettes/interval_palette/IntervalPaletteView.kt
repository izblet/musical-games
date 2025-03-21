package com.example.musicalgames.components.palettes.interval_palette

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.app.NotificationCompat.Style
import com.example.musicalgames.components.palettes.KeyboardBasedPalette
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval


class IntervalPaletteView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : KeyboardBasedPalette(context, attributeSet, defStyle) {
    private var listener: IntervalPaletteListener? = null
    override fun keyLabel(note: ChromaticNote): String {
       val interval = Interval.fromSemitones(note.ordinal)
       return interval.name
    }
    private fun getNoteFromInterval(interval: Interval) : ChromaticNote {
        return ChromaticNote.entries[interval.getSemitones()]
    }
    private fun getIntervalFromNote(note: ChromaticNote) : Interval {
        return Interval.fromSemitones(note.ordinal)
    }

    fun registerListener(listener: IntervalPaletteListener) {
        this.listener = listener
    }

    override fun onClickAction(note: ChromaticNote) {
       listener?.onClicked(Interval.fromSemitones(note.ordinal))
    }

    fun isGrayedOut(interval: Interval) : Boolean {
        return isGrayedOut(getNoteFromInterval(interval))
    }

    fun setGrayedOut(interval: Interval) {
        setGrayedOut(getNoteFromInterval(interval))
    }

    fun unsetGrayedOut(interval: Interval) {
        Log.d("interval", "unset grayed out")
        unsetGrayedOut(getNoteFromInterval(interval))
    }

    fun getGrayedOutIntervals() : Set<Interval> {
        return getGrayedOut().mapTo(mutableSetOf()){getIntervalFromNote(it)}
    }

}