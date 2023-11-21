package com.deejayen.note.ui.noteList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.repository.NoteRepository

class NoteListViewModel(private val noteRepository: NoteRepository) : ViewModel() {


    private val _notesWithDetailsList = MutableLiveData<List<NoteWithDetail>>()
    val notesWithDetailsList: LiveData<List<NoteWithDetail>> get() = _notesWithDetailsList


    fun getAllNoteWithDetail() {
        noteRepository.getAllNoteWithDetail().observeForever {
            _notesWithDetailsList.value = it
        }
    }

    suspend fun insertOrUpdateNoteWithDetailList(noteWithDetailArrList: ArrayList<NoteWithDetail>): ArrayList<NoteWithDetail> {
        return noteRepository.insertOrUpdateNoteWithDetailList(noteWithDetailArrList)
    }

    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        return noteRepository.deleteNoteWithDetail(noteWithDetail)
    }


}