package com.example.musicalgames.game.database
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiUtil {
    val adapterFactory = PolymorphicJsonAdapterFactory
        .of(Level::class.java, "level_type")
        .withSubtype(MentalLevel::class.java, "mental")
        .withSubtype(PlayEarLevel::class.java, "ear")
        .withSubtype(FlappyLevel::class.java, "flappy")

    val moshi = Moshi.Builder()
        .add(adapterFactory)
        .add(KotlinJsonAdapterFactory())
        .build()
    val adapters = mapOf(
        Game.FLAPPY to moshi.adapter<Level>(FlappyLevel::class.java),
        Game.PLAY_BY_EAR to moshi.adapter<Level>(PlayEarLevel::class.java),
        Game.MENTAL_INTERVALS to moshi.adapter<Level>(MentalLevel::class.java)
    )
}