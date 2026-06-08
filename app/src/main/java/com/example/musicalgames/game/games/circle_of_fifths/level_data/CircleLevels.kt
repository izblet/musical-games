package com.example.musicalgames.game.games.circle_of_fifths.level_data

import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.utils.Mode

object CircleLevels {
    val baseLevels: List<TaggedLevel> = listOf(
        level("Major - name to position", "", listOf(Mode.IONIAN)),
        level("Minor - name to position", "", listOf(Mode.AEOLIAN)),
        level("Dorian - name to position", "", listOf(Mode.DORIAN)),
        level("Mixolydian - name to position", "", listOf(Mode.MIXOLYDIAN)),
        level("Major, minor", "name to position", listOf(Mode.IONIAN, Mode.AEOLIAN)),
    )

    private fun level(name: String, description: String, modes: List<Mode>): TaggedLevel =
        TaggedLevel(Game.CIRCLE, 0, name, description, CircleLevel(positionToName = false, modes), isFavourite = false, isCustom = false)
}
