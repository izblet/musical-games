package com.example.musicalgames.games.chase
import android.animation.AnimatorSet
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.animation.ValueAnimator
import android.bluetooth.BluetoothDevice
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentGameChaseBinding
import com.example.musicalgames.utils.Note
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.games.chase.keyboard.KeyboardAdapter
import com.example.musicalgames.wrappers.bluetooth.BluetoothConnectionManager
import com.example.musicalgames.wrappers.bluetooth.BluetoothEventListener
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager

class GameFragment : Fragment(), BluetoothEventListener {
    companion object {
        const val MIN_KEY = "E3"
        const val KEY_NUM = 18
        const val KEYBOARD_DISABLE_MS = 100L
        const val JUMP_HEIGHT = 200f
        const val JUMP_MS = 500L
        const val JUMP_RANGE = KEY_NUM
        const val END_SCORE = 10
    }

    private lateinit var binding: FragmentGameChaseBinding
    private lateinit var dotImageView: ImageView

    private val soundPlayer by lazy { DefaultSoundPlayerManager(requireContext()) }
    private lateinit var opponent: BluetoothConnectionManager

    private var currentField: Int? = null
    private var score = 0
    private var opponentScore = 0
    private var playerTurn = true

    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameChaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun updateScores() {
        binding.playerScoreTextView.text = getString(R.string.your_score_string, viewModel.score.toString())
        binding.opponentScoreTextView.text = getString(R.string.opponent_score_string, viewModel.opponentScore.toString())

        binding.turnStatusTextView.text =
            if(playerTurn)
                getString(R.string.your_turn_string)
            else
                getString(R.string.opponent_turn_string)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dotImageView = view.findViewById(R.id.dot)
        val keyboardRecyclerView : RecyclerView = view.findViewById(R.id.keyboardRecyclerView)
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
        playerTurn = viewModel.server!!
        opponent = viewModel.bluetoothManager!!

        if(!opponent.connected())
            onDisconnected(java.lang.Exception("Opponent not connected"))

        opponent.bluetoothSubscribe(this)

        updateScores()

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        keyboardRecyclerView.layoutManager = layoutManager

        keyboardRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //we want to set the position of the dot only after the keyboard is set
                keyboardRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                dotImageView.y = (keyboardRecyclerView.top - dotImageView.height).toFloat()
            }
        })

        //the piano keys have to be scaled depending on the size of the screen
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val minKey = MusicUtil.midi(MIN_KEY)
        val keyWidth = screenWidth / KEY_NUM
        val pianoKeys = mutableListOf<Note>()
        for (i in 0 until KEY_NUM)
            pianoKeys.add(Note(minKey+i))


        val adapter = KeyboardAdapter(pianoKeys, keyWidth)
        keyboardRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { position->
            if(playerTurn) {
                playerTurn=false
                updateScores()
                val keyNote = pianoKeys[position]
                soundPlayer.play(keyNote.midiCode)
                currentField = null
                val keyView = layoutManager.findViewByPosition(position)
                keyView?.let {
                    val targetX = it.x + it.width / 2 - dotImageView.width / 2
                    val targetY = (keyboardRecyclerView.top - dotImageView.height).toFloat()
                    val maxY = targetY - JUMP_HEIGHT
                    val animatorSet = getAnimation(targetX, targetY, maxY)

                    animatorSet.start()
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            currentField = position
                            opponent.sendMessage(position)
                        }
                    })
                }
            }
        }
    }
    override fun onMessageReceived(message: Int) {
        requireActivity().runOnUiThread {
            if (message == R.integer.GAME_END) {
                score++
                if(score== END_SCORE) {
                    endGame()
                }
                toast("you found the opponent")
                updateScores()
            }
            else {

                playerTurn = true
                if (currentField == message) {
                    opponentScore++
                    if(opponentScore== END_SCORE) {
                        endGame()
                    }
                    toast("opponent found you")
                    opponent.sendMessage(R.integer.GAME_END)
                }
                updateScores()
            }
        }
    }

    private fun endGame() {
        viewModel.score=score
        viewModel.opponentScore = opponentScore
        opponent.bluetoothUnsubscribe()

        findNavController().navigate(R.id.action_gameFragment2_to_gameEndedFragment3)
    }

    override fun onDestroy() {
        super.onDestroy()
        opponent.bluetoothUnsubscribe()
    }

    override fun onDisconnected(exception: Exception) {
        Log.e("disconnect", "Disconnect")
        if(isAdded) {
            requireActivity().runOnUiThread {
                opponent.bluetoothUnsubscribe()
                toast("you got disconnected")
                activity?.onBackPressed()
            }
        }
    }

    //TODO: the interfaces should be divided - the following are not needed here
    override fun onDevicePaired() {}
    override fun onConnected() {}
    override fun onDeviceFound(device: BluetoothDevice?) {}

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
        val durationYUp = JUMP_MS /2
        animatorYUp.duration = durationYUp

        val animatorYDown = ValueAnimator.ofFloat(maxY, targetY)
        animatorYDown.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            dotImageView.y = value
        }
        animatorYDown.interpolator = AccelerateInterpolator()
        val durationYDown = JUMP_MS /2
        animatorYDown.duration = durationYDown

        animatorYDown.startDelay = durationYUp

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorX, animatorYUp, animatorYDown)
        return animatorSet
    }
    private fun toast(message: String) {
        requireActivity().runOnUiThread{
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }
}
