package com.example.musicalgames.game.games.play_by_ear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.InputMethod
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game_activity.ScreenHighlighter
import com.example.musicalgames.music_model.Note
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference
import com.example.musicalgames.utils.wrappers.sound_playing.DefaultSoundPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class EarRenderState(
    val message: String = "",
    val keyboardEnabled: Boolean = false,
    //true for the whole time the root note or melody is sounding aloud - the controller watches
    //this to know when to stop forwarding input (and, on the falling edge, when to reset/cooldown
    //the input source) rather than this view model making any input-trust decisions itself
    val playbackActive: Boolean = false
)

class EarViewModel() : ViewModel() {

    fun setLevel(level: Level, gameplay: GamePlayInstance) {
        this.level = level as PlayEarLevel
        this.gameplay = gameplay
        rootNote = level.getDisplayedRoot()
        available = this.level!!.keyList.map { Note(it) }
    }

    //TODO: level will be passed in constructor prbbly, otherwise change this
    var level: PlayEarLevel? = null
    var gameplay: GamePlayInstance = GamePlayInstance()
    var rootNote: Note? = null
    private var available: List<Note> = listOf()

    var problem : List<Note> = listOf()
    private var index : Int = 0
    var score = 0
    private var screenHighlighter: ScreenHighlighter? = null

    fun setScreenHighlighter(highlighter: ScreenHighlighter) {
        screenHighlighter = highlighter
    }
    private var questionActive = false
    //distinguishes "no problem asked yet" from "problem finished" - both leave questionActive
    //false, but only the latter should count as problemFinished()
    private var problemStarted = false

    private val _renderState = MutableStateFlow(EarRenderState())
    val renderState: StateFlow<EarRenderState> = _renderState.asStateFlow()

    private var soundPlayer: DefaultSoundPlayerManager? = null
    //still tracked locally (not just via renderState) so playRoot()/playProblem() can guard
    //against re-triggering playback while already playing - a game-logic concern, distinct from
    //whether input should be trusted right now, which the controller decides for itself
    private var rootPlaying = false
    private var problemPlaying = false

    fun setPlayer(pl : DefaultSoundPlayerManager) {
        soundPlayer = pl
    }

    //null plays each note recording to its natural end; set this once note recordings are
    //longer than what a melody should actually wait for, to cut them off after this many ms
    private var noteDurationMs: Long? = null

    fun setNoteDurationMs(durationMs: Long?) {
        noteDurationMs = durationMs
    }

    fun newProblem() {
        _renderState.value = _renderState.value.copy(message = "Play the melody")
        index = 0
        questionActive = true
        problemStarted = true
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
        _renderState.value = _renderState.value.copy(message = "Listen to the melody...", keyboardEnabled = false, playbackActive = true)
        viewModelScope.launch {
            soundPlayer!!.playSequence(problem, ::onPlaybackFinished, noteDurationMs)
        }
    }

    //the controller is responsible for not calling this at all while playback is active or
    //during its post-playback cooldown (see EarRenderState.playbackActive) - this is purely
    //game logic now, not an input-trust decision
    fun selectNote(note: Note) {
        if (gameplay.inputMethod == InputMethod.ONSCREEN) {
            soundPlayer!!.playNote(note.midiCode, null, noteDurationMs)
        }

        if (!questionActive) {
            return
        }
        if (problem[index] != note) {
            screenHighlighter?.wrong()
            val playedNote = NoteSpelling.spell(note, SpellingPreference.SHARPS)
            _renderState.value = _renderState.value.copy(message = "Wrong! The correct note was ${getCorrectNote()}. You played $playedNote.")
            questionActive = false
            return
        }
        screenHighlighter?.correct()
        index++
        if (index == problem.size) {
            questionActive = false
            score++
            _renderState.value = _renderState.value.copy(message = "Good!")
        }
    }

    fun problemFinished() : Boolean {
        return problemStarted && !questionActive
    }

    private fun getRandomNote(): Note {
        return available.random()
    }

    private fun onPlaybackFinished() {
        if(rootPlaying) {
            rootPlaying = false
        } else if(problemPlaying) {
            problemPlaying = false
        }
        _renderState.value = _renderState.value.copy(keyboardEnabled = true, message = "Play the melody", playbackActive = false)
    }

    fun playRoot() {
        if((!problemPlaying)&&(!rootPlaying)&&(rootNote!=null)) {
            //TODO: the following assumes that we have at least one note available, this should be checked somewhere
            rootPlaying = true
            _renderState.value = _renderState.value.copy(playbackActive = true)
            soundPlayer!!.playNote(rootNote!!.midiCode, ::onPlaybackFinished, noteDurationMs)
        }
    }

    fun getCorrectNote() : String {
        if(index<problem.size)
            return NoteSpelling.spell(problem[index], SpellingPreference.SHARPS)
        return ""
    }
}