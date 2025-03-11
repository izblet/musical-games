package com.example.musicalgames.games.mental_intervals

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game.games.mental_intervals.MentalViewmodelListener
import com.example.musicalgames.game_activity.IntentSettable
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import kotlin.random.Random

class MentalViewModel : ViewModel(), IntentSettable {
    companion object : GameIntentMaker {

        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is MentalLevel)
                throw Exception("Level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra("level", level)
            }
        }

    }
    override fun setDataFromIntent(intent: Intent) {
        level = intent.getParcelableExtra<MentalLevel>("level")
        availableNotes = level!!.startingNotes
        availableIntervals = level!!.intervals
        _type = level!!.mode
    }

    var level: MentalLevel? = null
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