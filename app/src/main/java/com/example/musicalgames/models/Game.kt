package com.example.musicalgames.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game (
    val name: String,
    val description: String,
    val iconResourceId: Int,
    val options: List<String>
) : Parcelable