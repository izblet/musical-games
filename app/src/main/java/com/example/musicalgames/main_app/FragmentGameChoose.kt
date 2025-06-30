package com.example.musicalgames.main_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CirclePaletteListener
import com.example.musicalgames.databinding.FragmentFirstBinding
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.utils.ChromaticNote

class FragmentGameChoose : Fragment(), CirclePaletteListener {
    //TODO: temporary this
    override fun onKeyClicked(root: ChromaticNote, major: Boolean) {
        Toast.makeText(requireContext(), root.toString(), Toast.LENGTH_SHORT).show()
    }

    private var _binding: FragmentFirstBinding? = null

    // this property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        //TODO: this is temporary
        binding.circleView.registerListener(this)


        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // chosen game will be saved in the viewModel
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        //this list shall be retrieved from a database
        val gameList = listOf(
            Game.FLAPPY,
            Game.PLAY_BY_EAR,
            Game.MENTAL_INTERVALS,
            Game.CIRCLE
        )

        val adapter = AdapterGameList(gameList, object : AdapterGameList.OnItemClickListener{
            override fun onItemClick(game: Game) {
                viewModel.game=game
                findNavController().navigate(R.id.action_FirstFragment_to_fragmentNewModeChoose)
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