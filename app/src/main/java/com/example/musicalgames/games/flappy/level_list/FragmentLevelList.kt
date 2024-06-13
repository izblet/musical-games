package com.example.musicalgames.games.flappy.level_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentFlappyLevelsBinding
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteName
import com.example.musicalgames.games.flappy.ViewModel
import com.example.musicalgames.games.GameOption


class FragmentLevelList : Fragment() {

        private var _binding: FragmentFlappyLevelsBinding? = null

        // this property is only valid between onCreateView and onDestroyView.
        private val binding get() = _binding!!
        private lateinit var viewModel: ViewModel

        override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
                _binding = FragmentFlappyLevelsBinding.inflate(inflater, container, false)

                val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerView)
                val layoutManager = LinearLayoutManager(context)
                recyclerView.layoutManager = layoutManager

                // chosen game will be saved in the viewModel
                viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
                val levelList = DefaultLevels.baseLevels

                val adapter = AdapterLevelList(levelList, object : AdapterLevelList.OnItemClickListener {
                        override fun onItemClick(level: Level) {
                                viewModel.gameType = GameOption.LEVELS
                                viewModel.minRange=level.minPitch
                                viewModel.maxRange=noteName(midi(level.minPitch)+level.keyNum-1)
                                viewModel.endAfter=level.endAfter
                                findNavController().navigate(R.id.action_fragmentLevelList_to_flappyGameFragment)
                        }
                })
                recyclerView.adapter = adapter

                return binding.root

        }

        class AdapterLevelList(private val levelList: List<Level>, private val onItemClickListener: OnItemClickListener) :
                RecyclerView.Adapter<AdapterLevelList.LevelViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
                        return LevelViewHolder(view)
                }

                override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
                        val level = levelList[position]
                        holder.bind(level)
                        holder.itemView.setOnClickListener {
                                onItemClickListener.onItemClick(level)
                        }
                }

                override fun getItemCount(): Int {
                        return levelList.size
                }

                inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
                        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
                        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
                        fun bind(level: Level) {
                                nameTextView.text = level.name
                                if(level.endAfter!= LEN_INF)
                                        descriptionTextView.text = "To pass: ${level.endAfter} pipes"
                                else
                                        descriptionTextView.text = "Arcade"
                        }
                }
                interface OnItemClickListener {
                        fun onItemClick(level: Level)
                }
        }

        override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
        }
}
