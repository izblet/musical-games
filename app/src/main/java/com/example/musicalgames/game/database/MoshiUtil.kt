package com.example.musicalgames.game.database
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.GameMap
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiUtil {
    // Derived from GameMap.registrations so every registered game's Level subtype is
    // automatically (de)serializable - no separate list to keep in sync by hand.
    val adapterFactory = GameMap.registrations.fold(
        PolymorphicJsonAdapterFactory.of(Level::class.java, "level_type")
    ) { factory, registration ->
        factory.withSubtype(registration.serialization.levelClass.java, registration.serialization.levelTypeTag)
    }

    val moshi = Moshi.Builder()
        .add(adapterFactory)
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapters = GameMap.registrations.associate {
        it.game to moshi.adapter<Level>(it.serialization.levelClass.java)
    }
}