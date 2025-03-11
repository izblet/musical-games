package com.example.musicalgames.games.mental_intervals

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.IntentSettable
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.game_activity.ViewModelListener
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import com.example.musicalgames.utils.Scale
import kotlin.random.Random

class MentalViewModel : ViewModel(), IntentSettable {
    companion object : GameIntentMaker {
        enum class Extra {
            INTERVALS,
            NOTES,
            MODE,
            SCALE //i think it should come back but not used for now
        }
        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is MentalLevel)
                throw Exception("Level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                val intervalArray =ArrayList(level.intervals.map{ it.getSemitones() })
                putExtra(Extra.INTERVALS.name, intervalArray)
                val noteArray = ArrayList(level.startingNotes.map { it.ordinal })
                putExtra(Extra.NOTES.name, noteArray)
                putExtra(Extra.MODE.name, level.mode.name)
                //putExtra(Extra.SCALE.name, level.scale.name)
            }
        }

    }
    override fun setDataFromIntent(intent: Intent) {
        availableNotes = intent.getIntegerArrayListExtra(Extra.NOTES.name)!!
            .map { ChromaticNote.fromDegree(it) }
        availableIntervals = intent.getIntegerArrayListExtra(Extra.INTERVALS.name)!!
            .map { Interval.fromSemitones(it)}
        _type = Type.valueOf(intent.getStringExtra(Extra.MODE.name)!!)
        //scale = Scale.valueOf(intent.getStringExtra(Extra.SCALE.name)!!)
    }

    //TODO: cannot change this for now but it should be changed
    var _score: Int = 0
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
    private var _messageText = ""

    val messageText get() = _messageText


    private var UI : ViewModelListener? = null
    private var endListener : GameListener? = null
    fun registerEndListener(listener: GameListener) { endListener=listener }
    fun registerUI(ui: ViewModelListener) { UI = ui }
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
            _messageText = "What is the note positioned at $interval from $questionNote?"
        else if(type == Type.NOTE_INTERVAL)
            _messageText = "What is the interval between $questionNote and $note"
        //else
        //    _messageText = "What is the degree $lastDegree in $questionNote $scale"

        UI?.onDataChanged()
    }
    fun select(note: ChromaticNote) {
        if(disabled)
            return

        if (note == this.note) {
            disabled=true
            score++
            _messageText = "Good!"
            Handler(Looper.getMainLooper()).postDelayed({
                generateQuestion()
            }, 1000)
        } else {
            _messageText = "The right answer is : ${this.note}"
            Handler(Looper.getMainLooper()).postDelayed({
                endListener?.onGameEnded()
            }, 1000)
        }
        UI?.onDataChanged()
    }
    fun select(interval: Interval) {
        if(disabled)
            return

        if (interval == this.interval) {
            disabled=true
            score++
            _messageText = "Good!"
            Handler(Looper.getMainLooper()).postDelayed({
                generateQuestion()
            }, 1000)
        } else {
            _messageText = "The right answer is : ${this.interval!!.name}"
            Handler(Looper.getMainLooper()).postDelayed({
                endListener?.onGameEnded()
            }, 1000)
        }
        UI?.onDataChanged()
    }
}