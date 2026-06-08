package com.example.musicalgames.utils

import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.play_by_ear.EarPlayLevels
import org.junit.Assert.assertTrue
import org.junit.Test

//forces level generation to run - PlayEarLevel's init{} validates that the
//root is displayed and that the interval is reachable, so any regression in
//the Note/NoteSpelling-based rewrite of LevelsPopulate/Populate throws here
class LevelGenerationSanityTest {

    @Test
    fun `play by ear levels generate without violating their invariants`() {
        val levels = EarPlayLevels.baseLevels
        assertTrue(levels.isNotEmpty())
        for (tagged in levels) {
            assertTrue("level name should not be blank: $tagged", tagged.name.isNotBlank())
        }
    }

    @Test
    fun `flappy levels generate without violating their invariants`() {
        val levels = FlappyLevels.baseLevels
        assertTrue(levels.isNotEmpty())
        for (tagged in levels) {
            assertTrue("level name should not be blank: $tagged", tagged.name.isNotBlank())
        }
    }

    @Test
    fun `generated play-by-ear level names contain reparseable note spellings`() {
        for (tagged in EarPlayLevels.baseLevels.take(20)) {
            //names look like "C MAJOR, C4 to C5" - the spelled notes must round-trip
            val spelledNotes = Regex("[A-G][#b]?-?\\d+").findAll(tagged.name).map { it.value }.toList()
            assertTrue("expected spelled notes in name '${tagged.name}'", spelledNotes.isNotEmpty())
            for (spelling in spelledNotes) {
                assertTrue("'$spelling' from name '${tagged.name}' should reparse", Note.parse(spelling) != null)
            }
        }
    }

    @Test
    fun `generated flappy level names contain reparseable note spellings`() {
        for (tagged in FlappyLevels.baseLevels.take(20)) {
            //names look like "C4 to C5, root: C major"
            val spelledNotes = Regex("[A-G][#b]?-?\\d+").findAll(tagged.name).map { it.value }.toList()
            assertTrue("expected spelled notes in name '${tagged.name}'", spelledNotes.isNotEmpty())
            for (spelling in spelledNotes) {
                assertTrue("'$spelling' from name '${tagged.name}' should reparse", Note.parse(spelling) != null)
            }
        }
    }
}
