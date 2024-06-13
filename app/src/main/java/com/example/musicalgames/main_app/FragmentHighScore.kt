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
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentHighScoreBinding
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GameOption
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
        val view = _binding?.root

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = HighScoreAdapter()
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        val gameName = GameMap.gameInfos[viewModel.game!!]!!.name
        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle("$gameName - high scores")
        val gameId = GameMap.gameInfos[viewModel.game!!]!!.id

        lifecycleScope.launch {
            val allHighScores = mutableListOf<HighScoreSection>()
            //TODO: this will be changed when other modes are introduced
            val option = GameOption.ARCADE
            //for(option in viewModel.gameOptions!!) {
                val highScores = viewModel.getHighScores(gameId, option)
                if(highScores.isNotEmpty())
                    allHighScores.add(HighScoreSection(option.toString(), highScores))
            //}
            adapter.setHighScoreSections(allHighScores)
        }
        return view
    }

    data class HighScoreSection(val mode: String, val highScores: List<HighScore>)

    inner class HighScoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var highScoreSections = emptyList<HighScoreSection>()

        inner class HighScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        }

        inner class ModeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val modeTextView: TextView = itemView.findViewById(R.id.modeTextView)
        }

        inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val emptyTextView: TextView = itemView.findViewById(R.id.emptyTextView)
        }

        override fun getItemViewType(position: Int): Int {
            return if (highScoreSections.isEmpty()) VIEW_TYPE_EMPTY
            else if (isHeader(position)) VIEW_TYPE_HEADER
            else VIEW_TYPE_ITEM
        }

        private fun isHeader(position: Int): Boolean {
            var count = 0
            for (section in highScoreSections) {
                if (position == count) return true
                count += section.highScores.size + 1
            }
            return false
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_HEADER -> {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_mode_header, parent, false)
                    ModeViewHolder(itemView)
                }
                VIEW_TYPE_ITEM -> {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_high_score, parent, false)
                    HighScoreViewHolder(itemView)
                }
                else -> {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_empty, parent, false)
                    EmptyViewHolder(itemView)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is EmptyViewHolder) {
                holder.emptyTextView.text = "No scores yet"
            } else if (isHeader(position)) {
                val modeViewHolder = holder as ModeViewHolder
                modeViewHolder.modeTextView.text = getHeaderForPosition(position)
            } else {
                val highScoreViewHolder = holder as HighScoreViewHolder
                val highScore = getHighScoreForPosition(position)
                val scorePosition = getScorePositionForPosition(position)
                highScoreViewHolder.scoreTextView.text = highScore.score.toString()
                highScoreViewHolder.itemView.findViewById<TextView>(R.id.labelTextView).text = when (scorePosition) {
                    0 -> "highest score:"
                    1 -> "2nd best score:"
                    2 -> "3rd best score:"
                    else -> ""
                }
            }
        }

        private fun getHeaderForPosition(position: Int): String {
            var count = 0
            for (section in highScoreSections) {
                if (position == count) return section.mode
                count += section.highScores.size + 1
            }
            throw IllegalStateException("Invalid position")
        }

        private fun getHighScoreForPosition(position: Int): HighScore {
            var count = 0
            for (section in highScoreSections) {
                if (position > count && position <= count + section.highScores.size) {
                    return section.highScores[position - count - 1]
                }
                count += section.highScores.size + 1
            }
            throw IllegalStateException("Invalid position")
        }

        private fun getScorePositionForPosition(position: Int): Int {
            var count = 0
            for (section in highScoreSections) {
                if (position > count && position <= count + section.highScores.size) {
                    return position - count - 1
                }
                count += section.highScores.size + 1
            }
            throw IllegalStateException("Invalid position")
        }

        override fun getItemCount(): Int {
            return if (highScoreSections.isEmpty()) 1
            else {
                var count = 0
                for (section in highScoreSections) {
                    count += section.highScores.size + 1
                }
                count
            }
        }

        fun setHighScoreSections(highScoreSections: List<HighScoreSection>) {
            this.highScoreSections = highScoreSections
            notifyDataSetChanged()
        }

    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_EMPTY = 2
    }
}
