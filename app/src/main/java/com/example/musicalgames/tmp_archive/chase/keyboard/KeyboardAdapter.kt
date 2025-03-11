package com.example.musicalgames.games.chase.keyboard
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.utils.Note

class KeyboardAdapter(private val keys: MutableList<Note>, private val keyWidth: Int) :
    RecyclerView.Adapter<KeyboardAdapter.KeyViewHolder>() {
    private var onItemClickListener: ((position:Int)->Unit)? = null
    private var disabled= false

    inner class KeyViewHolder(val pianoKey: PianoKey) : RecyclerView.ViewHolder(pianoKey)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
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

    //Not used for now but might be in the future
    fun setDisable(disable: Boolean) {
        disabled=disable
    }

    override fun getItemCount(): Int {
        return keys.size
    }
}
