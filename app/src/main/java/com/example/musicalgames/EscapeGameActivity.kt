package com.example.musicalgames
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
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
                val targetX = it.x + it.width/2 - dotImageView.width/2
                val targetY = (keyboardRecyclerView.top - dotImageView.height).toFloat()
                // Create ValueAnimator for x-coordinate animation
                val animatorX = ValueAnimator.ofFloat(dotImageView.x, targetX)
                animatorX.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    dotImageView.x = value
                }
                animatorX.interpolator = AccelerateDecelerateInterpolator()
                val durationX = 500L // Adjust duration as needed
                animatorX.duration = durationX

                // Create ValueAnimator for y-coordinate animation (move up)
                val animatorYUp = ValueAnimator.ofFloat(targetY, targetY - 100f) // Adjust jump height as needed
                animatorYUp.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    dotImageView.y = value
                }
                animatorYUp.interpolator = AccelerateDecelerateInterpolator()
                val durationYUp = 250L // Adjust duration as needed
                animatorYUp.duration = durationYUp

                // Create ValueAnimator for y-coordinate animation (move down)
                val animatorYDown = ValueAnimator.ofFloat(targetY - 100f, targetY) // Adjust jump height as needed
                animatorYDown.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    dotImageView.y = value
                }
                animatorYDown.interpolator = AccelerateDecelerateInterpolator()
                val durationYDown = 250L // Adjust duration as needed
                animatorYDown.duration = durationYDown

                // Create AnimatorSet to synchronize animations
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(animatorX, animatorYUp) // Start x and y animations together

                // Start the animations
                animatorSet.start()

                // Start the down animation after the up animation completes
                animatorYDown.startDelay = durationYUp
                animatorYDown.start()
            }
        }

    }

    private fun enablePianoKeys(enable: Boolean) {
        // Enable or disable piano keys
    }
}
