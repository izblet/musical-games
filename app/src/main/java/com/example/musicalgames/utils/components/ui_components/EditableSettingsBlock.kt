package com.example.musicalgames.utils.components.ui_components

import android.graphics.Rect

/**
 * A settings UI unit that starts locked and is edited via an explicit edit button, with changes
 * only taking effect once the screen resolves a "save changes?" prompt - never silently on
 * every interaction. Lets one screen-level overlay/dialog mechanism (see FragmentSettings)
 * drive any number of differently-shaped blocks (a single slider, a list of colour swatches,
 * etc.) through the same edit/confirm/discard lifecycle.
 */
interface EditableSettingsBlock {
    /** Fired when the edit button is tapped while not editing - only reachable when no other
     * block is mid-edit, since the screen's overlay blocks every other touch until then. */
    var onEditRequested: (() -> Unit)?

    /** Fired when the edit button is tapped again while already editing - same "save changes?"
     * prompt as tapping outside the block. */
    var onEditButtonReTapped: (() -> Unit)?

    /** Fired whenever an edit session ends, however it ended (confirmed or discarded) - lets
     * the screen tear down its cross-block overlay/tracking from one place. */
    var onEditEnded: (() -> Unit)?

    fun beginEdit()

    /** Called by the screen when the user picks "Save" in the confirmation prompt. */
    fun confirmEdit()

    /** Called by the screen when the user picks "Discard" in the confirmation prompt. */
    fun discardEdit()

    /** On-screen bounds, used by the screen's overlay to tell "tap inside this block" (let it
     * through) apart from "tap outside" (prompt save/discard). */
    fun getBoundsOnScreen(outRect: Rect)
}
