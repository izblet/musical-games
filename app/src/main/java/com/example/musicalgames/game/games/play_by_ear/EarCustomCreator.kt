package com.example.musicalgames.games.play_by_ear
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import com.example.musicalgames.R
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator


class EarCustomCreator(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {

    private lateinit var scrollLayout: ScrollView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_ear_custom_creator, this, true)

        scrollLayout = getChildAt(0) as ScrollView
        val scaleSpinner: Spinner = findViewById(R.id.scaleSpinner)
        val scaleOptions = arrayOf("Major", "Minor")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, scaleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        scaleSpinner.adapter = adapter


        val submitButton = scrollLayout.findViewById<View>(R.id.submitButton)
        submitButton.setOnClickListener {

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measureChild(scrollLayout, widthMeasureSpec, heightMeasureSpec)

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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val paddingStart = paddingLeft
        val paddingTop = paddingTop

        scrollLayout.layout(paddingStart, paddingTop, paddingStart + scrollLayout.measuredWidth, paddingTop + scrollLayout.measuredHeight)
    }
}