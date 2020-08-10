package com.amitbansal.notesapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.models.Note
import kotlinx.android.synthetic.main.note_item.view.*

class NotesAdapter() : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onNotesClickListener: ((Note) -> Unit)? = null

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemView.apply {
            tvNoteTitle.text = currentNote.title
            tvNoteText.text = currentNote.text
            if (currentNote.sync) {
                ivTick.setImageResource(R.drawable.ic_baseline_check_24)
            } else {
                ivTick.setImageResource(R.drawable.ic_baseline_close_24)
            }
            setOnClickListener {
                onNotesClickListener?.let { listener ->
                    listener(currentNote)
                }
            }
        }
    }

    fun setOnNotesClickListener(listener: (Note) -> Unit) {
        onNotesClickListener = listener
    }
}