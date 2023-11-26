package com.deejayen.note.ui.noteList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.R
import com.deejayen.note.database.entity.Note
import com.deejayen.note.databinding.ItemNoteListBinding


class NoteListRecyclerAdapter : RecyclerView.Adapter<NoteListRecyclerAdapter.ViewHolder>() {

    val noteList: ArrayList<Note> = arrayListOf()
    var callback: NoteListListener? = null

    interface NoteListListener {
        fun onClickNote(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.renderView(noteList[position])
        holder.binding.root.setOnClickListener {
            callback?.onClickNote(noteList[position])
        }
    }

    override fun getItemCount(): Int = noteList.size

    fun setNoteWithDetailList(newNoteList: ArrayList<Note>) {
        noteList.clear()
        noteList.addAll(newNoteList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemNoteListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun renderView(note: Note) {
            val itemNoteListTitleTv = binding.itemNoteListTitleTv
            val context = itemNoteListTitleTv.context
            var title = note.title
            if (title.isNullOrBlank()) {
                title = context.getString(R.string.empty)
                itemNoteListTitleTv.setTextColor(ContextCompat.getColor(context, R.color.black20))
            } else{
                itemNoteListTitleTv.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            binding.itemNoteListTitleTv.text = title

        }
    }
}
