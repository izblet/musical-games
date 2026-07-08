package com.example.musicalgames.game.games.circle_of_fifths.level_data

import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel
import com.example.musicalgames.music_model.Mode

object CircleLevels {
    val baseLevels: List<TaggedLevel> = listOf(
        nameToPosition("Major - name to position", "", listOf(Mode.IONIAN)),
        nameToPosition("Minor - name to position", "", listOf(Mode.AEOLIAN)),
        nameToPosition("Dorian - name to position", "", listOf(Mode.DORIAN)),
        nameToPosition("Mixolydian - name to position", "", listOf(Mode.MIXOLYDIAN)),
        nameToPosition("Major, minor - name to position", "", listOf(Mode.IONIAN, Mode.AEOLIAN)),
        positionToName("Major - position to name", "", listOf(Mode.IONIAN)),
        positionToName("Minor - position to name", "", listOf(Mode.AEOLIAN)),
        positionToName("Dorian - position to name", "", listOf(Mode.DORIAN)),
        positionToName("Mixolydian - position to name", "", listOf(Mode.MIXOLYDIAN)),
        positionToName("Major, minor - position to name", "", listOf(Mode.IONIAN, Mode.AEOLIAN)),
    )

    private fun nameToPosition(name: String, description: String, modes: List<Mode>): TaggedLevel =
        TaggedLevel(Game.CIRCLE, 0, name, description, CircleLevel(positionToName = false, modes), isFavourite = false, isCustom = false)

    private fun positionToName(name: String, description: String, modes: List<Mode>): TaggedLevel =
        TaggedLevel(Game.CIRCLE, 0, name, description, CircleLevel(positionToName = true, modes), isFavourite = false, isCustom = false)
}
