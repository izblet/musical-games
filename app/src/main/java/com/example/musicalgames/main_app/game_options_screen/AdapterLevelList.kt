package com.example.musicalgames.main_app.game_options_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R

class AdapterLevelList(private var levelList: List<TaggedLevel>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterLevelList.LevelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levelList[position]
        holder.bind(level)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(level)
        }
    }

    fun setData(newList: List<TaggedLevel>) {
        levelList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return levelList.size
    }

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val favouriteImageView: ImageView = itemView.findViewById(R.id.favouriteIconView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(level: TaggedLevel) {
            favouriteImageView.setImageResource(
                if(level.isFavourite) {
                    R.drawable.ic_favourite_filled
                }
                else {
                    R.drawable.ic_favourite_outline
                }
            )
            favouriteImageView.setOnClickListener {
                onItemClickListener.onFavouriteClick(level)
            }

            val binImageView = itemView.findViewById<ImageView>(R.id.binIconView)
            binImageView.setOnClickListener{
                onItemClickListener.onBinClick(level)
            }
            val chevronImageView = itemView.findViewById<ImageView>(R.id.chevronIconView)
            if(!level.isDeletable()) {
                binImageView.visibility = View.GONE
                chevronImageView.visibility = View.VISIBLE
            } else {
                binImageView.visibility = View.VISIBLE
                chevronImageView.visibility = View.GONE
            }

            nameTextView.text = level.name
            descriptionTextView.text = level.description
        }
    }
    interface OnItemClickListener {
        fun onItemClick(level: TaggedLevel)
        fun onFavouriteClick(level: TaggedLevel)
        fun onBinClick(level: TaggedLevel)
    }
}