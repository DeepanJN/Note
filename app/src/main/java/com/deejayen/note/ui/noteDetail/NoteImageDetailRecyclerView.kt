package com.deejayen.note.ui.noteDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.databinding.ItemImageDetailBinding
import com.squareup.picasso.Picasso
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NoteImageDetailRecyclerAdapter @Inject constructor(private val picasso: Picasso) : RecyclerView.Adapter<NoteImageDetailRecyclerAdapter.ViewHolder>() {

    var noteImageDetailList: List<NoteImageDetail> = emptyList()
    var callback: NoteImageDetailListener? = null

    interface NoteImageDetailListener {
        fun onClickImageNote(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(noteImageDetailList[position])
    }

    override fun getItemCount(): Int = noteImageDetailList.size

    fun setNoteWithDetailList(newNoteList: List<NoteImageDetail>) {
        noteImageDetailList = newNoteList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemImageDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(noteImageDetail: NoteImageDetail) {
            val filepath = noteImageDetail.value
            picasso.load("file:$filepath").into(binding.itemImageView)
        }
    }
}
