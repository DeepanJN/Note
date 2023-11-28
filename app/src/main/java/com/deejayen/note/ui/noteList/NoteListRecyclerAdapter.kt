package com.deejayen.note.ui.noteList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.R
import com.deejayen.note.database.entity.Note
import com.deejayen.note.databinding.ItemNoteListBinding

class NoteListRecyclerAdapter : RecyclerView.Adapter<NoteListRecyclerAdapter.ViewHolder>() {

    var noteList: List<Note> = emptyList()
    var callback: NoteListListener? = null

    interface NoteListListener {
        fun onClickNote(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(noteList[position])
    }

    override fun getItemCount(): Int = noteList.size

    fun setNoteWithDetailList(newNoteList: List<Note>) {
        noteList = newNoteList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemNoteListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(note: Note) {
            val itemNoteListTitleTv = binding.itemNoteListTitleTv
            val context = itemNoteListTitleTv.context
            var title = note.title
            val colorId = if (title.isNullOrBlank()) {
                title = context.getString(R.string.no_title)
                R.color.black20
            } else {
                R.color.black
            }
            itemNoteListTitleTv.text = title
            itemNoteListTitleTv.setTextColor(ContextCompat.getColor(context, colorId))

            binding.root.setOnClickListener {
                callback?.onClickNote(note)
            }

        }
    }
}
