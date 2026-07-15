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
import com.example.musicalgames.utils.components.key_establishment.KeyEstablishmentPlayer
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

    //the message currently reflecting actual game state (as opposed to the transient "root
    //note" overlay set while a root replay is sounding) - restored once that overlay clears,
    //so a manual root replay via the button never permanently clobbers answer feedback
    private var phaseMessage = ""
    private var rootFinishedCallback: (() -> Unit)? = null

    fun setPlayer(pl : DefaultSoundPlayerManager) {
        soundPlayer = pl
    }

    private var keyEstablishmentPlayer: KeyEstablishmentPlayer? = null

    fun setKeyEstablishmentPlayer(player: KeyEstablishmentPlayer) {
        player.onEndAction = ::onPlaybackFinished
        player.onMessageChangeAction = { msg -> _renderState.value = _renderState.value.copy(message = msg) }
        keyEstablishmentPlayer = player
    }

    //null plays each note recording to its natural end; set this once note recordings are
    //longer than what a melody should actually wait for, to cut them off after this many ms
    private var noteDurationMs: Long? = null

    fun setNoteDurationMs(durationMs: Long?) {
        noteDurationMs = durationMs
    }

    fun newProblem() {
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
        phaseMessage = "Listen to the melody..."
        _renderState.value = _renderState.value.copy(message = phaseMessage, keyboardEnabled = false, playbackActive = true)
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
            phaseMessage = "Wrong! The correct note was ${getCorrectNote()}. You played $playedNote. Play root twice for next question."
            _renderState.value = _renderState.value.copy(message = phaseMessage)
            questionActive = false
            return
        }
        screenHighlighter?.correct()
        index++
        if (index == problem.size) {
            questionActive = false
            score++
            phaseMessage = "Good! Play root twice for next question."
            _renderState.value = _renderState.value.copy(message = phaseMessage)
        }
    }

    fun problemFinished() : Boolean {
        return problemStarted && !questionActive
    }

    private fun getRandomNote(): Note {
        return available.random()
    }

    private fun onPlaybackFinished() {
        val wasRootPlaying = rootPlaying
        if(rootPlaying) {
            rootPlaying = false
        } else if(problemPlaying) {
            problemPlaying = false
            phaseMessage = "Play the melody"
        }
        _renderState.value = _renderState.value.copy(keyboardEnabled = true, message = phaseMessage, playbackActive = false)
        if (wasRootPlaying) {
            val callback = rootFinishedCallback
            rootFinishedCallback = null
            callback?.invoke()
        }
    }

    fun playRoot(onFinished: (() -> Unit)? = null) {
        if((!problemPlaying)&&(!rootPlaying)&&(rootNote!=null)) {
            //TODO: the following assumes that we have at least one note available, this should be checked somewhere
            rootPlaying = true
            rootFinishedCallback = onFinished
            _renderState.value = _renderState.value.copy(playbackActive = true)
            keyEstablishmentPlayer?.play(soundPlayer!!)
        } else {
            onFinished?.invoke()
        }
    }

    fun getCorrectNote() : String {
        if(index<problem.size)
            return NoteSpelling.spell(problem[index], SpellingPreference.SHARPS)
        return ""
    }
}