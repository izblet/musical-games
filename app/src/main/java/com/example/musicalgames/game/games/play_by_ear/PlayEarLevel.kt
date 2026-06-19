package com.example.musicalgames.game.games.play_by_ear

import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Note
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayEarLevel (
    val minPitchDisplayed: Int, //TODO: this should be calculated later, remove this field and the next one
    val maxPitchDisplayed: Int,
    val root: ChromaticNote,
    val problemLen: Int,
    val maxSemitoneInterval: Int,
    val keyList: List<Int>,

): Level() {
    init{
        require(minPitchDisplayed<maxPitchDisplayed) { "minimal pitch has to be smaller than maximal pitch: $minPitchDisplayed >= $maxPitchDisplayed"}
        require(isIntervalPossible()) { "some notes cannot be reached from others with the provided interval"}
        require(keyList.isNotEmpty()) { "The key list is empty" }
        require(isRootDisplayed()) { "The root is not visible on the screen"}
    }
    private fun isIntervalPossible() : Boolean {
        //you have to be able to reach the note above and the note below, doesn't make sense otherwise
        val isPossible = keyList.zipWithNext()
            .all {
            (a,b) -> a + maxSemitoneInterval >= b
        }
       return isPossible
    }
    private fun isRootDisplayed(): Boolean {
        for(note in minPitchDisplayed..maxPitchDisplayed) {
            if(Note(note).noteChromatic == root)
                return true
        }
        return false
    }

    fun getDisplayedRoot():Note? {
        for(note in minPitchDisplayed..maxPitchDisplayed) {
            if(Note(note).noteChromatic == root)
                return Note(note)
        }
        return null
    }
}
