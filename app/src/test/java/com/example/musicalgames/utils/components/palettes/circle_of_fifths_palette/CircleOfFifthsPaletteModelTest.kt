package com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette

import org.junit.Assert.*
import org.junit.Test

class CircleOfFifthsPaletteModelTest {

    @Test
    fun `defaults are empty highlights and no text`() {
        val model = CircleOfFifthsPaletteModel()
        assertEquals(listOf<Int>(), model.highlightedIndices)
        assertNull(model.centerText)
        assertNull(model.centerTextColor)
    }

    @Test
    fun `setting highlightedIndices invokes onChange exactly once`() {
        val model = CircleOfFifthsPaletteModel()
        var changeCount = 0
        model.onChange = { changeCount++ }

        model.highlightedIndices = listOf(1, 4)

        assertEquals(1, changeCount)
        assertEquals(listOf(1, 4), model.highlightedIndices)
    }

    @Test
    fun `setting highlightColor invokes onChange exactly once`() {
        val model = CircleOfFifthsPaletteModel()
        var changeCount = 0
        model.onChange = { changeCount++ }

        model.highlightColor = 0xFF00FF00.toInt()

        assertEquals(1, changeCount)
        assertEquals(0xFF00FF00.toInt(), model.highlightColor)
    }

    @Test
    fun `setting centerText invokes onChange exactly once`() {
        val model = CircleOfFifthsPaletteModel()
        var changeCount = 0
        model.onChange = { changeCount++ }

        model.centerText = "C major"

        assertEquals(1, changeCount)
        assertEquals("C major", model.centerText)
    }

    @Test
    fun `setting centerTextColor invokes onChange exactly once`() {
        val model = CircleOfFifthsPaletteModel()
        var changeCount = 0
        model.onChange = { changeCount++ }

        model.centerTextColor = 0xFFFF0000.toInt()

        assertEquals(1, changeCount)
        assertEquals(0xFFFF0000.toInt(), model.centerTextColor)
    }

    @Test
    fun `onChange is not invoked for mutations before it is registered`() {
        val model = CircleOfFifthsPaletteModel()
        model.centerText = "before registering a listener"

        var changeCount = 0
        model.onChange = { changeCount++ }

        assertEquals(0, changeCount)
    }

    @Test
    fun `a newly assigned onChange only fires on subsequent mutations`() {
        val model = CircleOfFifthsPaletteModel()
        var firstListenerCalls = 0
        model.onChange = { firstListenerCalls++ }
        model.centerText = "first"

        var secondListenerCalls = 0
        model.onChange = { secondListenerCalls++ }
        model.centerText = "second"

        assertEquals(1, firstListenerCalls)
        assertEquals(1, secondListenerCalls)
    }
}
