package com.example.musicalgames.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.models.Note
import com.example.musicalgames.views.PianoKey

class KeyboardAdapter(private val keys: MutableList<Note>, private val keyWidth: Int) :
    RecyclerView.Adapter<KeyboardAdapter.KeyViewHolder>() {
    private var onItemClickListener: ((position:Int)->Unit)? = null
    private var disabled= false

    //TODO:
    inner class KeyViewHolder(val pianoKey: PianoKey) : RecyclerView.ViewHolder(pianoKey)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.key_item, parent, false)
        val view = PianoKey(parent.context)
        val layoutParams = view.layoutParams
        layoutParams.width = keyWidth
        view.layoutParams = layoutParams
        return KeyViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
        val note = keys[position]
        holder.pianoKey.setNote(note)
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
