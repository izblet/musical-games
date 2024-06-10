package com.example.musicalgames.games.flappy

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.R
import kotlinx.coroutines.launch

class GameEndedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_ended, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        // Set up any necessary logic here

        // Find views
        val textScore = view.findViewById<TextView>(R.id.text_score)
        val buttonExit = view.findViewById<Button>(R.id.button_exit)

        // Set the score text
        textScore.text = "Your Score: ${viewModel.score}"

        lifecycleScope.launch {
            val isHighScore = viewModel.checkHighScore()
            // Update the message on the endgame screen based on the result
            if (isHighScore) {
                view.findViewById<TextView>(R.id.text_high_score).text = "New High Score!"
            } else {
                view.findViewById<TextView>(R.id.text_high_score).visibility = View.GONE
            }
        }

        // Set click listener for the exit button
        buttonExit.setOnClickListener {
           requireActivity().finish()
        }
    }

}
