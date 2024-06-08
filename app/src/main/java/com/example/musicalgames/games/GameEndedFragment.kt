package com.example.musicalgames.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.R
import com.example.musicalgames.games.flappy.ViewModel

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

        // Set click listener for the exit button
        buttonExit.setOnClickListener {
           requireActivity().finish()
        }
    }

}
