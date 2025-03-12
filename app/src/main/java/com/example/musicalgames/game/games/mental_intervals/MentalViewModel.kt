package com.example.musicalgames.games.mental_intervals

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game.games.mental_intervals.MentalViewmodelListener
import com.example.musicalgames.game_activity.GameViewModel
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import kotlin.random.Random

class MentalViewModel : ViewModel(), GameViewModel {

    override fun setLevel(level: Level) {

        this._level = level as MentalLevel
        availableNotes = this.level.startingNotes
        availableIntervals = this.level.intervals
        _type = this.level.mode
    }

    private var _level: MentalLevel? = null
    val level get() = _level!!

    var score = 0

    private var _type : Type = Type.INTERVAL_NOTE
    val type get() = _type

    private var disabled = true
    private var availableIntervals: List<Interval>? = null
    private var availableNotes: List<ChromaticNote>? = null
    private var questionNote: ChromaticNote? = null
    private var interval: Interval? = null
    private var lastDegree: Int = 0
    private var note: ChromaticNote? = null


    private var _UI : MentalViewmodelListener? = null
    private var endListener : GameListener? = null
    fun registerEndListener(listener: GameListener) { endListener=listener }
    fun registerUI(ui: MentalViewmodelListener) { _UI = ui }
    private val UI get() = _UI!!
    fun startGame() { generateQuestion() }
    private fun getRandomInterval():Interval {
        val i = Random.nextInt(availableIntervals!!.size)
        lastDegree = i+2
        return availableIntervals!![i]
    }
    private fun getRandomNote():ChromaticNote {
        val i = Random.nextInt(availableNotes!!.size)
        return availableNotes!![i]
    }
    private fun generateQuestion() {
        disabled=false
        val interval = getRandomInterval()
        questionNote = getRandomNote()
        val questionNoteIndex = questionNote!!.ordinal
        val noteIndex = (questionNoteIndex + interval.getSemitones()) % ChromaticNote.valuesSize()
        note = ChromaticNote.fromDegree(noteIndex)

        if(type == Type.INTERVAL_NOTE)
            UI.onNewProblem(interval, questionNote!!)
        else if(type == Type.NOTE_INTERVAL)
            UI.onNewProblem(questionNote!!, note!!)

    }
    fun select(note: ChromaticNote) {
        if(disabled)
            return

        if (note == this.note) {
            disabled=true
            score++
            UI.onRightAnswer()
            Handler(Looper.getMainLooper()).postDelayed({
                generateQuestion()
            }, 1000)

        } else {
            UI.onWrongAnswer(this.note!!)
            Handler(Looper.getMainLooper()).postDelayed({
                endListener?.onGameEnded()
            }, 1000)
        }
    }
    fun select(interval: Interval) {
        if(disabled)
            return

        if (interval == this.interval) {
            disabled=true
            score++
            UI.onRightAnswer()
            Handler(Looper.getMainLooper()).postDelayed({
                generateQuestion()
            }, 1000)
        } else {
            UI.onWrongAnswer(this.interval!!)
            Handler(Looper.getMainLooper()).postDelayed({
                endListener?.onGameEnded()
            }, 1000)
        }
    }
}