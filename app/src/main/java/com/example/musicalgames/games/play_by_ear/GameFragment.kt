package com.example.musicalgames.games.play_by_ear
import android.animation.AnimatorSet
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.animation.ValueAnimator
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentGamePlayByEarBinding
import com.example.musicalgames.utils.Note
import com.example.musicalgames.games.chase.keyboard.KeyboardAdapter
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager

class GameFragment : Fragment() {
   private lateinit var binding: FragmentGamePlayByEarBinding

    private val soundPlayer by lazy { DefaultSoundPlayerManager(requireContext()) }

    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamePlayByEarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keyboardRecyclerView : RecyclerView = view.findViewById(R.id.keyboardRecyclerView)
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        keyboardRecyclerView.layoutManager = layoutManager

        //the piano keys have to be scaled depending on the size of the screen
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val minKey = viewModel.minKey!!.midiCode
        val maxKey = viewModel.maxKey!!.midiCode
        val keyNum = maxKey - minKey +1
        val keyWidth = screenWidth / (keyNum)
        val pianoKeys = mutableListOf<Note>()
        for (i in 0 until keyNum)
            pianoKeys.add(Note(minKey+i))

        val adapter = KeyboardAdapter(pianoKeys, keyWidth)
        keyboardRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { position->
                val keyNote = pianoKeys[position]
                soundPlayer.play(keyNote.midiCode)
            }
        }
    }

