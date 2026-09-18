package com.joybrata.notepad.presentation

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joybrata.notepad.databinding.ListItemBinding
import com.joybrata.notepad.domain.model.Note

/**
 * Efficient RecyclerView adapter backed by [ListAdapter] + DiffUtil.
 * Uses view binding for the item layout.
 */
class NoteAdapter : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    fun noteAt(position: Int): Note = getItem(position)

    inner class NoteViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.textView.text = note.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
                oldItem == newItem
        }
    }
}
