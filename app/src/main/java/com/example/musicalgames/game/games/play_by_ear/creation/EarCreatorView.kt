package com.example.musicalgames.game.games.play_by_ear.creation
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TableRow
import android.widget.Toast
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.musicalgames.R
import com.example.musicalgames.components.keyboard.KeyboardSelectSheet
import com.example.musicalgames.components.keyboard.KeyboardView
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.utils.Note


class EarCreatorView(context: Context, createLevelAction: (Level)->Unit, attrs: AttributeSet?) : CustomGameCreator(context, createLevelAction, attrs) {

    private var scrollLayout: ScrollView
    private var mainLayout: ConstraintLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.view_ear_custom_creator, this, true)

        mainLayout = getChildAt(0) as ConstraintLayout
        scrollLayout = mainLayout.getChildAt(0) as ScrollView
        val scaleSpinner: Spinner = findViewById(R.id.scaleSpinner)
        val scaleOptions = arrayOf("Major", "Minor")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, scaleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        scaleSpinner.adapter = adapter
        val saveButton = findViewById<Button>(R.id.saveButton)
        val isSelectionToggle = findViewById<ToggleButton>(R.id.isSelectionToggle)

        val noteRow = findViewById<TableRow>(R.id.custom_notes_row)
        val scaleRow = findViewById<TableRow>(R.id.scale_row)
        val noteSelectorContainer = findViewById<FrameLayout>(R.id.keyboard_container)
        val noteSelector = findViewById<KeyboardView>(R.id.keyboard_preview)
        noteSelector.setRange(
            Note("C4"),Note("B4")) //TODO: should definitely not be hardcoded
        noteSelector.setGrayedOut()
        noteSelector.setOutlined(false)
        noteSelector.setDisabled(true)


        val setSelectionMethod: ()->Unit = {
            if(isSelectionToggle.isChecked) {
                noteRow.visibility = View.VISIBLE
                scaleRow.visibility = View.GONE
            } else {
                noteRow.visibility = View.GONE
                scaleRow.visibility = View.VISIBLE
            }
        }
        setSelectionMethod()

        isSelectionToggle.setOnClickListener{
            setSelectionMethod()
        }


        noteSelectorContainer.setOnClickListener {
            val onSelectAction: (Set<Int>)->Unit = {
                grayedOut: Set<Int> ->
                noteSelector.setGrayedOutSet(grayedOut)
            }
            val bottomSheet = KeyboardSelectSheet(context, onSelectAction, noteSelector.getGrayedOut())
            bottomSheet.show()
        }

        //val submitButton = mainLayout.findViewById<View>(R.id.submitButton)

        /*submitButton.setOnClickListener {
            val lvl = tryMakeLevel()
            if(lvl != null) {
                createLevelAction(lvl)
            }

        }
        */
    }

    private fun getFieldVal(id: Int): String {
        return scrollLayout.findViewById<EditText>(id).text.toString()
    }

    private fun getSpinnerVal(id: Int): String {
        return scrollLayout.findViewById<Spinner>(id).selectedItem.toString()
    }

    private fun tryMakeLevel(): Level? {
        /*
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
        //it should return null instead of throwing

        val keylistNote = MusicUtil.getScaleNotesFrom(scaleEnum, rootChromatic, Note(min_Int), pitches_num)
        val keylist = keylistNote.map { it.midiCode }

        Toast.makeText(context, "len: ${len}, pitches: ${keylist}", Toast.LENGTH_SHORT).show()

        return PlayEarLevel(-1, min_Int, max_Int, rootChromatic, len, max_interval, keylist, name, description)
        */
        return null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measureChild(mainLayout, widthMeasureSpec, heightMeasureSpec)

        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun getLevel(): Level? {
        try {
            val level = tryMakeLevel()
            return level
        } catch (e: Exception) {
            return null
        }
    }

    override fun highlightMissing() {
        Toast.makeText(context, "Some fields are missing", Toast.LENGTH_SHORT).show()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val paddingStart = paddingLeft
        val paddingTop = paddingTop

        mainLayout.layout(
            paddingStart,
            paddingTop,
            paddingStart + mainLayout.measuredWidth,
            paddingTop + mainLayout.measuredHeight
        )

    }

}