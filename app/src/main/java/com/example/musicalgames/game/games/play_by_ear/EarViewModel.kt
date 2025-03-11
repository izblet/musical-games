package com.example.musicalgames.games.play_by_ear

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game.games.play_by_ear.EarViewmodelListener
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.game_activity.IntentSettable
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.Note
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.wrappers.sound_playing.SoundPlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class EarViewModel() : ViewModel(),IntentSettable, SoundPlayerListener {
    //TODO: the following should be moved to GameFactory (only after all games have levels as argumets)
    companion object : GameIntentMaker {
        override fun getIntent(activity: FragmentActivity, level: Level): Intent {
            if(level !is PlayEarLevel)
                throw Exception("Level is of wrong type")

            return Intent(activity, GameActivity::class.java).apply {
                putExtra("level", level)
            }
        }

    }

    override fun setDataFromIntent(intent: Intent) {
        level = intent.getParcelableExtra("level")
        available = level!!.keyList.map { Note(it) }
    }
    //########

    //TODO: level will be passed in constructor prbbly, otherwise change this
    var level: PlayEarLevel? = null
    private var available: List<Note> = listOf()

    var problem : List<Note> = listOf()
    private var index : Int = 0
    var score = 0

    private var listener: EarViewmodelListener? = null
    private var soundPlayer: DefaultSoundPlayerManager? = null
    fun setPlayer(pl : DefaultSoundPlayerManager) {
        soundPlayer = pl
    }
    fun registerListener(listener: EarViewmodelListener) {
        this.listener = listener
    }

    fun newProblem() {
        listener!!.onNewProblem()
        index = 0
        generateProblem()
        playProblem()
    }

    private fun generateProblem() {
        val notes = mutableListOf(getRandomNote())
        while (notes.size < level!!.problemLen) {
            val newNote = getRandomNote()
            if (abs(notes[notes.size - 1].midiCode - newNote.midiCode) <= level!!.maxSemitoneInterval)
                notes.add(newNote)
        }
        problem = notes
    }

    private fun playProblem() {
        listener!!.onPlaybackStarted()
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            soundPlayer!!.playSequence(problem, this@EarViewModel)
        }
    }

    fun selectNote(note: Note) {
        soundPlayer!!.play(note.midiCode)
        if (index >= problem.size)
            return

        if (problem[index] != note) {
            listener!!.onWrongAnswer()
            return
        }
        index++
        if (index == problem.size) {
            score++
            listener!!.onRightAnswer()
        }
    }

    fun isProblemSolved() : Boolean {
        return index == problem.size
    }

    fun getRandomNote(): Note {
        return available.random()
    }

    override fun onSoundFinished() {
       listener!!.onPlaybackFinished()
    }

    fun playRoot() {
        //TODO: the following assumes that we have at least one note available, this should be checked somewhere
        soundPlayer!!.play(MusicUtil.midi(level!!.root.name+available.get(0).octave))
    }

    fun getCorrectNote() : String {
        if(index<problem.size)
            return problem[index].name
        return ""
    }
}