package com.example.musicalgames.main_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentFlappyLevelsBinding
import com.example.musicalgames.games.Game
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.games.flappy.FlappyViewModel as FlappyViewModel
import com.example.musicalgames.games.flappy.level_list.DefaultLevels
import com.example.musicalgames.games.flappy.level_list.Level


class FragmentLevelList : Fragment() {

        private var _binding: FragmentFlappyLevelsBinding? = null

        // this property is only valid between onCreateView and onDestroyView.
        private val binding get() = _binding!!
        private lateinit var viewModel: ViewModel
        private fun updateTitle() {
                val title = "${GameMap.gameInfos[viewModel.game!!]!!.name} - Levels"
                (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(title)
        }
        override fun onResume() {
                super.onResume()
                updateTitle()
        }
        override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View {
                _binding = FragmentFlappyLevelsBinding.inflate(inflater, container, false)

                val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerView)
                val layoutManager = LinearLayoutManager(context)
                recyclerView.layoutManager = layoutManager

                // chosen game was saved in the viewModel
                viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
                updateTitle()

                var levelList: List<Level>? = null
                var intentMaker: GameIntentMaker? = null

                if(viewModel.game == Game.FLAPPY) {
                        levelList = DefaultLevels.baseLevels
                        intentMaker = FlappyViewModel.Companion
                }

                val adapter = AdapterLevelList(levelList!!, object : AdapterLevelList.OnItemClickListener {
                        override fun onItemClick(level: Level) {
                                val intent = intentMaker!!.getIntent(activity!!, level)
                                if(intent.getStringExtra(GameActivity.ARG_GAME_TYPE) != null) {
                                        throw Exception("game type argument is already set in intent")
                                }
                                intent.putExtra(GameActivity.ARG_GAME_TYPE, viewModel.game!!.name)
                                startActivity(intent)
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
                                descriptionTextView.text = level.description
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
