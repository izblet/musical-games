package com.example.musicalgames
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.adapters.KeyboardAdapter

class EscapeGameActivity : AppCompatActivity() {
    private lateinit var dotImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_escape)

        dotImageView= findViewById(R.id.dot)

        // Set listener to handle animation end

        // Set up RecyclerView for keyboard
        val keyboardRecyclerView: RecyclerView = findViewById(R.id.keyboardRecyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        keyboardRecyclerView.layoutManager = layoutManager

        // Calculate the width of each key dynamically
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val numKeys = 12 // Total number of keys in the keyboard
        val keyWidth = screenWidth / numKeys

        // Create a list of colors for each key
        val pianoKeyColors = mutableListOf<Int>()
        for (i in 0 until numKeys) {
            // For simplicity, every third key is black. Adjust this logic as needed.
            if (i % 3 != 0) {
                pianoKeyColors.add(Color.BLACK)
            } else {
                pianoKeyColors.add(Color.WHITE)
            }
        }

        val adapter = KeyboardAdapter(pianoKeyColors, keyWidth)
        keyboardRecyclerView.adapter = adapter
        adapter.setOnItemClickListener { position->
            val keyView = layoutManager.findViewByPosition(position)
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
            keyView?.let {
                val dotX = it.x + it.width/2 - dotImageView.width/2
                val dotY = keyboardRecyclerView.top - dotImageView.height
                dotImageView.x = dotX
                dotImageView.y = dotY.toFloat()
            }
        }

    }

    private fun enablePianoKeys(enable: Boolean) {
        // Enable or disable piano keys
    }
}
