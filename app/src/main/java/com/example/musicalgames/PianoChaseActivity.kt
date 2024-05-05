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
import com.example.musicalgames.models.Note
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.views.PianoKey
import com.example.musicalgames.wrappers.FallbackSoundPlayerManager

class PianoChaseActivity : AppCompatActivity() {
    //TODO: change this class into pianoChaseView so that creating the activities doesn't kill you
    private lateinit var dotImageView: ImageView
    private val soundPlayer = FallbackSoundPlayerManager(this)
    fun getAnimation(targetX: Float, targetY:Float, maxY:Float): AnimatorSet {
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

        animatorYDown.startDelay = durationYUp
        // Create AnimatorSet to synchronize animations
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorX, animatorYUp, animatorYDown) // Start x and y animations together
        return animatorSet
    }

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
        val numKeys = 18 // Total number of keys in the keyboard
        val minKey = MusicUtil.midi("C4")
        val keyWidth = screenWidth / numKeys

        // Create a list of colors for each key
        val pianoKeys = mutableListOf<Note>()
        for (i in 0 until numKeys)
            pianoKeys.add(Note(minKey+i))

        keyboardRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                keyboardRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Set the position of dotImageView above the keyboardRecyclerView
                dotImageView.y = (keyboardRecyclerView.top - dotImageView.height).toFloat()
            }
        })

        val adapter = KeyboardAdapter(pianoKeys, keyWidth)
        keyboardRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { position->
            val keyNote = pianoKeys[position]
            soundPlayer.play(keyNote.frequency)
            val keyView = layoutManager.findViewByPosition(position)
            keyView?.let {
                val targetX = it.x + it.width/2 - dotImageView.width/2
                val targetY = (keyboardRecyclerView.top - dotImageView.height).toFloat()
                val maxY = targetY -200f
                var animatorSet = getAnimation(targetX, targetY, maxY)
                // Start the animations
                // Start the down animation after the up animation completes
                adapter.setDisable(true)
                animatorSet.start()
                animatorSet.addListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        adapter.setDisable(false)
                    }
                })
            }
        }

    }
}
