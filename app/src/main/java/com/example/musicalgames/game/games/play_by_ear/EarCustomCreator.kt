package com.example.musicalgames.games.play_by_ear
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import com.example.musicalgames.R
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.Note
import com.example.musicalgames.utils.Scale


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
            val lvl = tryMakeLevel()
            if(lvl != null) {
                createLevelAction(lvl)
            }

        }
    }

    private fun getFieldVal(id: Int) : String {
        return scrollLayout.findViewById<EditText>(id).text.toString()
    }
    private fun getSpinnerVal(id: Int) : String {
        return scrollLayout.findViewById<Spinner>(id).selectedItem.toString()
    }

    private fun tryMakeLevel() : Level? {
        val name = getFieldVal(R.id.nameInput)
        val description = getFieldVal(R.id.descriptionInput)

        val len = getFieldVal(R.id.lenInput).toInt()

        val root = getFieldVal(R.id.rootNoteInput)
        val rootChromatic = ChromaticNote.fromString(root)

        val scale = getSpinnerVal(R.id.scaleSpinner)
        val scaleEnum = Scale.valueOf(scale.uppercase())

        val max_interval = getFieldVal(R.id.maxIntervalInput).toInt()

        val min_sound = getFieldVal(R.id.minSoundInput)
        val min_Int = MusicUtil.midi(min_sound)

        val max_sound = getFieldVal(R.id.maxSoundInput)
        val max_Int = MusicUtil.midi(max_sound)

        val pitches_num = max_Int-min_Int

        //TODO: this function has no checks, it desperately needs them (it is even in the function name)

        val keylistNote = MusicUtil.getScaleNotesFrom(scaleEnum, rootChromatic, Note(min_Int), pitches_num)
        val keylist = keylistNote.map { it.midiCode }

        Toast.makeText(context, "len: ${len}, pitches: ${keylist}", Toast.LENGTH_SHORT).show()

        return PlayEarLevel(-1, min_Int, max_Int, rootChromatic, len, max_interval, keylist, name, description)
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