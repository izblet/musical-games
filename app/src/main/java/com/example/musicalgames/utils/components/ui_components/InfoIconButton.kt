package com.example.musicalgames.utils.components.ui_components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import com.example.musicalgames.R

/**
 * A "?" icon that, when tapped, shows [helpText] in a centered dialog (dismissed by its OK
 * button or by tapping outside, both standard [AlertDialog] behavior). Generic and
 * self-contained so it can be dropped into any layout - not tied to any one setting/screen.
 */
class InfoIconButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var helpText: String = ""

    init {
        setImageResource(R.drawable.ic_help_outline)
        contentDescription = "Help"
        setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(helpText)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun configure(helpText: String) {
        this.helpText = helpText
    }
}
