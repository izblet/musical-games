package com.example.musicalgames.games.mental_intervals

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.example.musicalgames.game_activity.AbstractViewModel
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.game_activity.ViewModelListener
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import kotlin.random.Random

class MentalViewModel(application: Application) : AbstractViewModel(application) {
    companion object : GameIntentMaker {
        enum class Extra {
            MAX_INTERVAL
        }
        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is MentalLevel)
                throw Exception("Level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra(Extra.MAX_INTERVAL.name,level.maxSemitoneInterval)
            }
        }

    }
    override fun setDataFromIntent(intent: Intent) {
        maxInterval = intent.getIntExtra(Extra.MAX_INTERVAL.name, 5)

    }

    //TODO: cannot change this for now but it should be changed
    var _score: Int = 0
    override var score = 0
    private var _intervalToName = true
    val intervalToName get() = _intervalToName

    private var disabled = true
    private var questionNote: ChromaticNote? = null
    private var interval: Interval? = null
    private var note: ChromaticNote? = null
    private var maxInterval: Int = Int.MAX_VALUE
    private var _messageText = ""

    val messageText get() = _messageText


    private var UI : ViewModelListener? = null
    private var endListener : GameListener? = null
    fun registerEndListener(listener: GameListener) { endListener=listener }
    fun registerUI(ui: ViewModelListener) { UI = ui }
    fun startGame() { generateQuestion() }
    private fun generateQuestion() {
        disabled=false
        val startNoteIndex = Random.nextInt(ChromaticNote.valuesSize())
        val semitoneInterval = Random.nextInt(1, maxInterval+1)
        interval =  Interval.fromSemitones(semitoneInterval)
        questionNote = ChromaticNote.fromDegree(startNoteIndex)
        val noteIndex = (startNoteIndex + semitoneInterval) % ChromaticNote.valuesSize()
        note = ChromaticNote.fromDegree(noteIndex)

        if(intervalToName)
            _messageText = "What is the note positioned at $interval from $questionNote?"
        else
            _messageText = "What is the interval between $questionNote and $note"

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