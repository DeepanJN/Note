package com.deejayen.note.ui.noteList

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.repository.NoteRepository

class NoteListViewModel(private val noteRepository: NoteRepository) : ViewModel() {


    private val _notesWithDetailsList = MutableLiveData<List<NoteWithDetail>>()
    val notesWithDetailsList: LiveData<List<NoteWithDetail>> get() = _notesWithDetailsList


    fun getAllNoteWithDetail(owner: LifecycleOwner) {
        noteRepository.getAllNoteWithDetail().observe(owner) {
            _notesWithDetailsList.value = it
        }
    }

    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        return noteRepository.deleteNoteWithDetail(noteWithDetail)
    }


}