package com.deejayen.note.ui.noteList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.database.NoteWithDetail
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
            binding.itemNoteListTitleTv.text = note.title ?: ""
        }
    }
}
