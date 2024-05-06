package com.example.musicalgames
import android.animation.AnimatorSet
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.adapters.KeyboardAdapter
import com.example.musicalgames.models.Note
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.wrappers.ConnectionSocket
import com.example.musicalgames.wrappers.ConnectionSocketListener
import com.example.musicalgames.wrappers.FallbackSoundPlayerManager
class PianoChaseGameFragment : Fragment(), ConnectionSocketListener {
    companion object {
        const val MIN_KEY = "C4"
        const val KEY_NUM = 18
        const val KEYBOARD_DISABLE_MS = 100L
        const val JUMP_HEIGHT = 200f
        const val JUMP_MS = 500L
        const val JUMP_RANGE = KEY_NUM
    }

    private lateinit var dotImageView: ImageView
    private lateinit var keyboardRecyclerView: RecyclerView
    private val soundPlayer by lazy { FallbackSoundPlayerManager(requireContext()) }
    private var opponent: ConnectionSocket? = null
    private var currentField: Int? = null
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_chase, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dotImageView = view.findViewById(R.id.dot)
        keyboardRecyclerView = view.findViewById(R.id.keyboardRecyclerView)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        keyboardRecyclerView.layoutManager = layoutManager

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val minKey = MusicUtil.midi(MIN_KEY)
        val keyWidth = screenWidth / KEY_NUM

        val pianoKeys = mutableListOf<Note>()
        for (i in 0 until KEY_NUM)
            pianoKeys.add(Note(minKey+i))

        keyboardRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                keyboardRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                dotImageView.y = (keyboardRecyclerView.top - dotImageView.height).toFloat()
            }
        })

        val adapter = KeyboardAdapter(pianoKeys, keyWidth)
        keyboardRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { position->
            val keyNote = pianoKeys[position]
            soundPlayer.play(keyNote.frequency)
            currentField=null
            val keyView = layoutManager.findViewByPosition(position)
            keyView?.let {
                val targetX = it.x + it.width/2 - dotImageView.width/2
                val targetY = (keyboardRecyclerView.top - dotImageView.height).toFloat()
                val maxY = targetY - JUMP_HEIGHT
                var animatorSet = getAnimation(targetX, targetY, maxY)

                adapter.setDisable(true)
                animatorSet.start()
                animatorSet.addListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        handler.postDelayed({adapter.setDisable(false)}, KEYBOARD_DISABLE_MS)
                        currentField=position
                        opponent?.sendMessage(position)
                    }
                })
            }
        }
    }

    private fun getAnimation(targetX: Float, targetY:Float, maxY:Float): AnimatorSet {
        val animatorX = ValueAnimator.ofFloat(dotImageView.x, targetX)
        animatorX.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            dotImageView.x = value
        }
        animatorX.interpolator = LinearInterpolator()
        val durationX = JUMP_MS
        animatorX.duration = durationX

        val animatorYUp = ValueAnimator.ofFloat(targetY, maxY)
        animatorYUp.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            dotImageView.y = value
        }
        animatorYUp.interpolator = DecelerateInterpolator()
        val durationYUp = JUMP_MS/2
        animatorYUp.duration = durationYUp

        val animatorYDown = ValueAnimator.ofFloat(maxY, targetY)
        animatorYDown.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            dotImageView.y = value
        }
        animatorYDown.interpolator = AccelerateInterpolator()
        val durationYDown = JUMP_MS/2
        animatorYDown.duration = durationYDown

        animatorYDown.startDelay = durationYUp

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorX, animatorYUp, animatorYDown)
        return animatorSet
    }

    override fun onMessage(i: Int) {
        if(i==R.integer.GAME_END) {
            //game over
        }
        if(currentField==i){
            //game over
            opponent?.sendMessage(R.integer.GAME_END)
        }
    }
}
