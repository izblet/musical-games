package com.example.musicalgames.main_app;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentFirstBinding
import com.example.musicalgames.databinding.FragmentHighScoreBinding
import com.example.musicalgames.games.HighScore
import kotlinx.coroutines.launch


class FragmentHighScore : Fragment() {

    private lateinit var adapter: HighScoreAdapter
    private var _binding: FragmentHighScoreBinding? = null

    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHighScoreBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = HighScoreAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        val gameId = GameMap.gameInfos[viewModel.game!!]!!.id

        lifecycleScope.launch {
            val highScores = viewModel.getHighScores(gameId)
            adapter.setHighScores(highScores)
        }
        return view
    }

    inner class HighScoreAdapter : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {

        private var highScores = emptyList<HighScore>()

        inner class HighScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_high_score, parent, false)
            return HighScoreViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: HighScoreViewHolder, position: Int) {
            val current = highScores[position]
            holder.scoreTextView.text = current.score.toString()
        }

        override fun getItemCount() = highScores.size

        fun setHighScores(highScores: List<HighScore>) {
            this.highScores = highScores
            notifyDataSetChanged()
        }
    }
}
