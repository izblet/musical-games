package com.example.musicalgames

import OptionsAdapter
import android.media.MediaCodec.LinearBlock
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalgames.databinding.FragmentSecondBinding
import com.example.musicalgames.models.Game

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
interface ToolbarTitleUpdater : IToolbarTitleUpdater {
    fun updateToolbarTitle(title: String)
}
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val game: Game = arguments?.getParcelable("game") ?: Game("","",0,emptyList())

        val navController = findNavController()
        val destination = navController.currentDestination

        (requireActivity() as? ToolbarTitleUpdater)?.updateToolbarTitle(game.name)
        val optionsAdapter = OptionsAdapter(game.options) {
            option -> Toast.makeText(requireContext(), "Clicked on $option", Toast.LENGTH_SHORT).show()
        }
        binding.optionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = optionsAdapter
        }

        /*binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}