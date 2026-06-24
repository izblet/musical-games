package com.example.musicalgames.game.game_core.creation

import android.os.Parcelable

abstract class Level () : Parcelable {
    // Games with no question-direction concept always support it; games whose answer isn't
    // always a single note (e.g. a circle position or an interval) override this per-direction.
    open fun supportsMicrophoneInput(): Boolean = true
}