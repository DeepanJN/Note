package com.deejayen.note.ui.noteDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.repository.NoteRepository

class NoteDetailViewModel(private val noteRepository: NoteRepository) : ViewModel() {


    suspend fun insertOrUpdateNoteWithDetailList(noteWithDetailArrList: ArrayList<NoteWithDetail>): ArrayList<NoteWithDetail> {
        return noteRepository.insertOrUpdateNoteWithDetailList(noteWithDetailArrList)
    }


}