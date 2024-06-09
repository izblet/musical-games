package com.example.musicalgames.main_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R

class AdapterGameOptions(
    private val options: List<GameOption>,
    private val onItemClick: (GameOption) -> Unit
) : RecyclerView.Adapter<AdapterGameOptions.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option.getString())
        holder.itemView.setOnClickListener { onItemClick(option) }
    }

    override fun getItemCount(): Int = options.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val optionTextView: TextView = itemView.findViewById(R.id.optionTextView)

        fun bind(option: String) {
            optionTextView.text = option
        }
    }
}
