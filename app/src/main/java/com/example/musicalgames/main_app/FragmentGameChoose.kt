package com.example.musicalgames.main_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentFirstBinding

class FragmentGameChoose : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // this property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager


        // chosen game will be saved in the viewModel
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        //this list shall be retrieved from a database
        val gameList: List<Game> = listOf(
            Game("Piano Chase",
                "Multiplayer chase game on a piano",
                R.drawable.default_game_icon,
                listOf("Arcade", "Levels", "Multiplayer")),
            Game("Flappy Bird",
                "Flappy bird game controlled with voice",
                R.drawable.default_game_icon,
                listOf("Arcade", "Levels")
            )
        )

        val adapter = AdapterGameList(gameList, object : AdapterGameList.OnItemClickListener{
            override fun onItemClick(game: Game) {
                viewModel.chosenGame=game
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
        })
        recyclerView.adapter = adapter
        return binding.root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}