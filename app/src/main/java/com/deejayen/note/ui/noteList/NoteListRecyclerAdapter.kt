package com.deejayen.note.ui.noteList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.databinding.ItemNoteListBinding
import com.deejayen.note.util.MockDataUtil


class NoteListRecyclerAdapter : RecyclerView.Adapter<NoteListRecyclerAdapter.ViewHolder>() {

    val noteWithDetailList: ArrayList<NoteWithDetail> = arrayListOf(MockDataUtil.getMockNoteWithDetail())
    var callback: NoteListListener? = null

    interface NoteListListener {
        fun onClickNote(noteWithDetail: NoteWithDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.renderView(noteWithDetailList[position])
        holder.binding.root.setOnClickListener {
            callback?.onClickNote(noteWithDetailList[position])
        }
    }

    override fun getItemCount(): Int = noteWithDetailList.size

    fun setNoteWithDetailList(newNoteDetailList: ArrayList<NoteWithDetail>) {
        noteWithDetailList.clear()
        noteWithDetailList.addAll(newNoteDetailList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemNoteListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun renderView(noteWithDetail: NoteWithDetail) {
            binding.itemNoteListTitleTv.text = noteWithDetail.note.title ?: ""
        }
    }
}
