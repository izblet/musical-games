package com.example.musicalgames.utils.components.palettes.interval_palette

import com.example.musicalgames.music_model.Interval

interface IntervalPaletteListener {
    fun onClicked(interval: Interval)
}