package com.example.musicalgames.games.chase;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.R

class GameEndedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_ended, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(MultiplayerViewModel::class.java)

        val headMessage = view.findViewById<TextView>(R.id.text_game_ended)
        val textScore = view.findViewById<TextView>(R.id.text_score)
        val textDescription = view.findViewById<TextView>(R.id.text_high_score)
        val buttonExit = view.findViewById<Button>(R.id.button_exit)

        if(viewModel.score> viewModel.opponentScore)
            headMessage.text = "You have won!"
        else
            headMessage.text = "You have lost!"

        textScore.text = "Your score: ${viewModel.score}"
        textDescription.text = "Opponent's score: ${viewModel.opponentScore}"
        buttonExit.setOnClickListener {
           requireActivity().finish()
        }
    }
}