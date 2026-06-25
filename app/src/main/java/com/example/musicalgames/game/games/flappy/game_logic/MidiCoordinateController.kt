package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.game.game_core.input.PitchSource
import com.example.musicalgames.music_model.MusicUtil

class MidiCoordinateController (
    private val pitchRecogniser: PitchSource,
    private val minCoordinate: Double,
    private val maxCoordinate: Double
){
    private val spiceAtMidi0 = MusicUtil.spice(0)
    private val spicePerSemitone = MusicUtil.spice(1) - spiceAtMidi0

    fun getCoordinate() : Double? {
        val pitch = pitchRecogniser.getPitch()
        if (pitch == PitchSource.UNDEFINED) return null

        val coordinate = (pitch - spiceAtMidi0) / spicePerSemitone

        if (coordinate !in minCoordinate..maxCoordinate) return null

        return coordinate
    }
}