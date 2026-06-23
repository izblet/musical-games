package com.example.musicalgames.game.games.play_by_ear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.InputMethod
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
    private var questionActive = false

    private val _renderState = MutableStateFlow(EarRenderState())
    val renderState: StateFlow<EarRenderState> = _renderState.asStateFlow()

    private var soundPlayer: DefaultSoundPlayerManager? = null
    private var rootPlaying = false
    private var problemPlaying = false

    //after audio playback "finishes", the mic can still be hearing the tail of it for a bit -
    //both the detector's own trailing window and any hardware/acoustic latency beyond that - so
    //EXTERNAL_INSTRUMENT input is ignored for a further cooldown past onPlaybackFinished(),
    //long enough to outlast the detector's window with a small safety margin. The window itself
    //is a tunable microphone setting, so the caller must report its actual configured value via
    //setMicrophoneWindowMs() - this can't default to a fixed value without risking drifting out
    //of sync with whatever window size is actually configured.
    private var externalInstrumentSettleMs = 0L
    private var acceptExternalInputAfterMs = 0L

    fun setMicrophoneWindowMs(windowMs: Long) {
        externalInstrumentSettleMs = windowMs + SETTLE_SAFETY_MARGIN_MS
    }

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
        //while the root note or the melody is playing aloud, ignore any input entirely - this
        //matters most for EXTERNAL_INSTRUMENT, where the mic is already listening at this point
        //and would otherwise pick up the device's own speaker output and "answer" the melody
        //with itself
        if (rootPlaying || problemPlaying) {
            return
        }
        if (gameplay.inputMethod == InputMethod.EXTERNAL_INSTRUMENT && System.currentTimeMillis() < acceptExternalInputAfterMs) {
            return
        }

        if (gameplay.inputMethod == InputMethod.ONSCREEN) {
            soundPlayer!!.playNote(note.midiCode, null)
        }

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
        acceptExternalInputAfterMs = System.currentTimeMillis() + externalInstrumentSettleMs
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

    companion object {
        private const val SETTLE_SAFETY_MARGIN_MS = 200L
    }
}