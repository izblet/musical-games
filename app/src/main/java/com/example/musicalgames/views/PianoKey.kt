package com.example.musicalgames.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.musicalgames.R
import com.example.musicalgames.models.Note
import com.example.musicalgames.utils.MusicUtil

class PianoKey@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var note: Note? = null

    fun setNote(note: Note) {
        this.note=note
        if(MusicUtil.isWhite(note))
            setBackgroundResource(R.drawable.white_key)
        else
            setBackgroundResource(R.drawable.black_key)
    }
    fun getFrequency() : Double?{
        return note?.frequency
    }

    init {
        id = R.id.keyView
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        //scaleType = ImageView.ScaleType.FIT_XY


    }
}