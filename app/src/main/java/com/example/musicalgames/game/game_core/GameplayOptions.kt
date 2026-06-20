package com.example.musicalgames.game.game_core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class GameplayOptions {
    TYPE, BPM, DISPLAY_SCORE, INPUT_METHOD, ESTABLISH_KEY_WITH
}
enum class GameplayType {
    ARCADE, INFINITE, LIMITED
}

enum class InputMethod {
    ONSCREEN, EXTERNAL_INSTRUMENT
}
enum class keyEstablishOption {
    ROOT, ROOT_CHORD
}
@Parcelize
data class GamePlayInstance(
    val type:GameplayType= GameplayType.INFINITE,
    val bpm:Int=120,
    val displayScore: Boolean = false,
    val inputMethod: InputMethod = InputMethod.ONSCREEN,
    val establishKeyWith: keyEstablishOption = keyEstablishOption.ROOT
) : Parcelable {
    init{
        require(bpm in getMinBpmValue()..getMaxBpmValue())
    }
    companion object {
        fun getMaxBpmValue(): Int {
            return 300
        }

        fun getMinBpmValue(): Int {
            return 100
        }
    }

}




