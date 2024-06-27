package com.example.musicalgames.game_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.flappy.FlappyViewModel
import com.example.musicalgames.games.play_by_ear.EarViewModel

class GameEndedFragment : Fragment() {
    private val args : GameEndedFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_ended, container, false)
    }
    private var game: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        game = args.game
        val headMessageText = args.headMessage
        val scoreText = args.scoreMessage
        val descriptionText = args.descriptionMessage


        val headMessage = view.findViewById<TextView>(R.id.text_game_ended)
        headMessage.text = headMessageText
        val textScore = view.findViewById<TextView>(R.id.text_score)
        textScore.text=scoreText
        val textDescription = view.findViewById<TextView>(R.id.text_high_score)
        textDescription.text = descriptionText

        val buttonExit = view.findViewById<Button>(R.id.button_exit)
        val buttonRetry = view.findViewById<Button>(R.id.button_retry)

        if(headMessageText.isEmpty())
            headMessage.visibility = View.GONE
        if(scoreText.isEmpty())
            textScore.visibility = View.GONE
        if(descriptionText.isEmpty())
            textDescription.visibility = View.GONE

        buttonExit.setOnClickListener {
            requireActivity().finish()
        }

        buttonRetry.setOnClickListener {
            val action =GameEndedFragmentDirections.actionGameEndedFragmentToFlappyGameFragment(game!!)
            findNavController().navigate(action)
        }
    }

}
