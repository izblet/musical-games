package com.example.musicalgames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class GameEndedFragment : Fragment() {

    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            score = it.getInt(ARG_SCORE, 0)
        }
    }

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

        // Set up any necessary logic here

        // Find views
        val textScore = view.findViewById<TextView>(R.id.text_score)
        val buttonExit = view.findViewById<Button>(R.id.button_exit)

        // Set the score text
        textScore.text = "Your Score: $score"

        // Set click listener for the exit button
        buttonExit.setOnClickListener {
            // Add logic to handle exit button click here
            // For example, navigate back or close the activity
        }
    }

    companion object {
        private const val ARG_SCORE = "score"

        @JvmStatic
        fun newInstance(score: Int): GameEndedFragment {
            val fragment = GameEndedFragment()
            val args = Bundle()
            args.putInt(ARG_SCORE, score)
            fragment.arguments = args
            return fragment
        }
    }
}
