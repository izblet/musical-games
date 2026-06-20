package com.example.musicalgames.game.games.play_by_ear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference
import com.example.musicalgames.utils.wrappers.sound_playing.DefaultSoundPlayerManager
import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class EarRenderState(
    val message: String = "",
    val keyboardEnabled: Boolean = false
)

class EarViewModel() : ViewModel(), SoundPlayerListener {

    fun setLevel(level: Level) {
        this.level = level as PlayEarLevel
        rootNote = level.getDisplayedRoot()
        available = this.level!!.keyList.map { Note(it) }
    }

    //TODO: level will be passed in constructor prbbly, otherwise change this
    var level: PlayEarLevel? = null
    var rootNote: Note? = null
    private var available: List<Note> = listOf()

    var problem : List<Note> = listOf()
    private var index : Int = 0
    var score = 0
    private var questionActive = false

    private val _renderState = MutableStateFlow(EarRenderState())
    val renderState: StateFlow<EarRenderState> = _renderState.asStateFlow()

    private var soundPlayer: DefaultSoundPlayerManager? = null
    private var rootPlaying = false
    private var problemPlaying = false

    fun setPlayer(pl : DefaultSoundPlayerManager) {
        soundPlayer = pl
    }

    fun newProblem() {
        _renderState.value = _renderState.value.copy(message = "Play the melody")
        index = 0
        questionActive = true
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
        problemPlaying = true
        _renderState.value = _renderState.value.copy(message = "Listen to the melody...", keyboardEnabled = false)
        viewModelScope.launch {
            soundPlayer!!.playSequence(problem, this@EarViewModel)
        }
    }

    fun selectNote(note: Note) {
        if((!rootPlaying)&&(!problemPlaying))
            soundPlayer!!.playNote(note.midiCode, null)

        if (!questionActive) {
            return
        }
        if (problem[index] != note) {
            _renderState.value = _renderState.value.copy(message = "Wrong! The correct note was ${getCorrectNote()}.")
            questionActive = false
            return
        }
        index++
        if (index == problem.size) {
            questionActive = false
            score++
            _renderState.value = _renderState.value.copy(message = "Good!")
        }
    }

    fun problemFinished() : Boolean {
        return !questionActive
    }

    private fun getRandomNote(): Note {
        return available.random()
    }

    override fun onPlaybackFinished() {
        if(rootPlaying) {
            rootPlaying = false
        } else if(problemPlaying) {
            problemPlaying = false
        }
        _renderState.value = _renderState.value.copy(keyboardEnabled = true, message = "Play the melody")
    }

    fun playRoot() {
        if((!problemPlaying)&&(!rootPlaying)&&(rootNote!=null)) {
            //TODO: the following assumes that we have at least one note available, this should be checked somewhere
            rootPlaying = true
            soundPlayer!!.playNote(rootNote!!.midiCode, this)
        }
    }

    fun getCorrectNote() : String {
        if(index<problem.size)
            return NoteSpelling.spell(problem[index], SpellingPreference.SHARPS)
        return ""
    }
}