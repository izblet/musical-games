package com.example.musicalgames.games.play_by_ear
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.example.musicalgames.R
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator


class EarCustomCreator(context: Context, attrs: AttributeSet?) : CustomGameCreator(context, attrs) {

    private lateinit var linearLayout: LinearLayout

    init {
        // Inflate the LinearLayout layout
        LayoutInflater.from(context).inflate(R.layout.view_ear_custom_creator, this, true)

        // Get reference to the LinearLayout child
        linearLayout = getChildAt(0) as LinearLayout
        val scaleSpinner: Spinner = findViewById(R.id.scaleSpinner)
        val scaleOptions = arrayOf("Major", "Minor")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, scaleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        scaleSpinner.adapter = adapter


        // You can now interact with the LinearLayout and its children
        // For example, setting up the submit button action
        val submitButton = linearLayout.findViewById<View>(R.id.submitButton)
        submitButton.setOnClickListener {
            val rootNote = linearLayout.findViewById<View>(R.id.rootNoteInput).toString()
            val scale = linearLayout.findViewById<View>(R.id.scaleSpinner).toString()
            // Handle input
            Toast.makeText(context, "Root Note: $rootNote, Scale: $scale", Toast.LENGTH_SHORT).show()
        }
    }

    // Measure the LinearLayout
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Measure the LinearLayout child
        measureChild(linearLayout, widthMeasureSpec, heightMeasureSpec)

        // Set the measured dimensions of the ViewGroup based on the child
        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun getLevel(): Level? {
        TODO("Not yet implemented")
    }

    override fun saveLevel() {
        TODO("Not yet implemented")
    }

    // Layout the LinearLayout child
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val paddingStart = paddingLeft
        val paddingTop = paddingTop

        // Layout the LinearLayout within the ViewGroup
        linearLayout.layout(paddingStart, paddingTop, paddingStart + linearLayout.measuredWidth, paddingTop + linearLayout.measuredHeight)
    }
}