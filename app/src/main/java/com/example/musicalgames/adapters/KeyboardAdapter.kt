package com.example.musicalgames.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R

class KeyboardAdapter(private val keys: MutableList<Int>, private val keyWidth: Int) :
    RecyclerView.Adapter<KeyboardAdapter.KeyViewHolder>() {
    private var onItemClickListener: ((position:Int)->Unit)? = null
    private var disabled= false

    //TODO:
    inner class KeyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.key_item, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.width = keyWidth
        view.layoutParams = layoutParams
        return KeyViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
        val keyView = holder.itemView.findViewById<View>(R.id.keyView)
        keyView.setBackgroundColor(keys[position])

        holder.itemView.setOnClickListener {
            if(!disabled)
                onItemClickListener?.invoke(position)
        }
    }
    fun setOnItemClickListener(listener: (position: Int)->Unit) {
        onItemClickListener=listener
    }
    fun setDisable(disable: Boolean) {
        disabled=disable
    }

    override fun getItemCount(): Int {
        return keys.size
    }
}
