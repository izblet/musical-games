package com.example.musicalgames.main_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentFirstBinding
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.GameMap

class FragmentGameChoose : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // this property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerView

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // chosen game will be saved in the main viewModel
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val gameInfosList = GameMap.gameInfos.entries.toList()

        val adapter = AdapterGameList(gameInfosList, object : AdapterGameList.OnItemClickListener{
            override fun onItemClick(game: Game) {
                viewModel.chooseGame(game)
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

class AdapterGameList(private val gameInfoList: List<Map.Entry<Game, GameInfo>>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterGameList.GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val entry = gameInfoList[position]
        val gameInfo = entry.value
        holder.bind(gameInfo)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(entry.key)
        }
    }

    override fun getItemCount(): Int {
        return gameInfoList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(gameInfo: GameInfo) {
            iconImageView.setImageResource(gameInfo.iconResourceId)
            nameTextView.text = gameInfo.name
            descriptionTextView.text = gameInfo.description
        }
    }
    interface OnItemClickListener {
        fun onItemClick(game: Game)
    }
}