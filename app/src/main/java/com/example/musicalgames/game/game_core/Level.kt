package com.example.musicalgames.game_activity

import android.os.Parcelable

abstract class Level (
    open val id: Int,
    open val name: String,
    open val description: String
) : Parcelable