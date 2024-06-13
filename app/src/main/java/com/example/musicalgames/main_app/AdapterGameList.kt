package com.example.musicalgames.main_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

class AdapterGameList(private val gameInfoList: List<Game>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterGameList.GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameInfoList[position]
        holder.bind(game)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(game)
        }
    }

    override fun getItemCount(): Int {
        return gameInfoList.size
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        fun bind(game: Game) {
            val gameInfo = GameMap.gameInfos[game]!!
            iconImageView.setImageResource(gameInfo.iconResourceId)
            nameTextView.text = gameInfo.name
            descriptionTextView.text = gameInfo.description
        }
    }
    interface OnItemClickListener {
        fun onItemClick(game: Game)
    }
}
