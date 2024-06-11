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
import com.example.musicalgames.main_app.GameOption
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
        val headMessage = view.findViewById<TextView>(R.id.text_game_ended)
        val textScore = view.findViewById<TextView>(R.id.text_score)
        val textDescription = view.findViewById<TextView>(R.id.text_high_score)
        val buttonExit = view.findViewById<Button>(R.id.button_exit)

        if(viewModel.gameType == GameOption.LEVELS) {

            if(viewModel.score == viewModel.endAfter) {
                headMessage.text = "You have passed the level!"
                textDescription.visibility=View.GONE
                textScore.visibility=View.GONE
            }
            else {
                textScore.text = "Your score: ${viewModel.score}"
                textDescription.text = "Score needed: ${viewModel.endAfter}"
            }

        }
        else {
            // Set the score text
            textScore.text = "Your Score: ${viewModel.score}"

            lifecycleScope.launch {
                val isHighScore = viewModel.checkHighScore()
                // Update the message on the endgame screen based on the result
                if (isHighScore) {
                    textDescription.text = "New High Score!"
                } else {
                    textDescription.visibility = View.GONE
                }
            }
        }

        // Set click listener for the exit button
        buttonExit.setOnClickListener {
           requireActivity().finish()
        }
    }

}
