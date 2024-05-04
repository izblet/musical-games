package com.example.musicalgames
import android.animation.AnimatorSet
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.adapters.KeyboardAdapter

class PianoChaseActivity : AppCompatActivity() {
    private lateinit var dotImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_chase)

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
        val numKeys = 24 // Total number of keys in the keyboard
        val keyWidth = screenWidth / numKeys

        val whitekeys = listOf<Int>(
            0,2,4,5,7,9,11
        )
        // Create a list of colors for each key
        val pianoKeyColors = mutableListOf<Boolean>()
        for (i in 0 until numKeys) {
            // For simplicity, every third key is black. Adjust this logic as needed.
            if (i % 12 in whitekeys) {
                pianoKeyColors.add(true)
            } else {
                pianoKeyColors.add(false)
            }
        }
        keyboardRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                keyboardRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Set the position of dotImageView above the keyboardRecyclerView
                dotImageView.y = (keyboardRecyclerView.top - dotImageView.height).toFloat()
            }
        })

        val adapter = KeyboardAdapter(pianoKeyColors, keyWidth)
        keyboardRecyclerView.adapter = adapter
        adapter.setOnItemClickListener { position->
            val keyView = layoutManager.findViewByPosition(position)
            keyView?.let {
                val targetX = it.x + it.width/2 - dotImageView.width/2
                val targetY = (keyboardRecyclerView.top - dotImageView.height).toFloat()
                val maxY = targetY -200f


                // Create ValueAnimator for x-coordinate animation
                val animatorX = ValueAnimator.ofFloat(dotImageView.x, targetX)
                animatorX.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    dotImageView.x = value
                }
                animatorX.interpolator = LinearInterpolator()
                val durationX = 500L // Adjust duration as needed
                animatorX.duration = durationX

                // Create ValueAnimator for y-coordinate animation (move up)
                val animatorYUp = ValueAnimator.ofFloat(targetY, maxY) // Adjust jump height as needed
                animatorYUp.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    dotImageView.y = value
                }
                animatorYUp.interpolator = DecelerateInterpolator()
                val durationYUp = 250L // Adjust duration as needed
                animatorYUp.duration = durationYUp

                // Create ValueAnimator for y-coordinate animation (move down)
                val animatorYDown = ValueAnimator.ofFloat(maxY, targetY) // Adjust jump height as needed
                animatorYDown.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    dotImageView.y = value
                }
                animatorYDown.interpolator = AccelerateInterpolator()
                val durationYDown = 250L // Adjust duration as needed
                animatorYDown.duration = durationYDown

                // Create AnimatorSet to synchronize animations
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(animatorX, animatorYUp) // Start x and y animations together

                // Start the animations
                animatorSet.start()

                // Start the down animation after the up animation completes
                animatorYDown.startDelay = durationYUp
                adapter.setDisable(true)
                animatorYDown.start()
                animatorSet.addListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        adapter.setDisable(false)
                    }
                })
            }
        }

    }

    private fun enablePianoKeys(enable: Boolean) {
        // Enable or disable piano keys
    }
}
