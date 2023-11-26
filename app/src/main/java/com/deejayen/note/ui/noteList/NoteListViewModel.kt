package com.deejayen.note.ui.noteList

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deejayen.note.database.entity.Note
import com.deejayen.note.repository.NoteRepository

class NoteListViewModel(private val noteRepository: NoteRepository) : ViewModel() {


    private val _notesList = MutableLiveData<List<Note>>()
    val notesList: LiveData<List<Note>> get() = _notesList

    fun getAllNote(lifecycleOwner: LifecycleOwner) {
        noteRepository.getAllNote().observe(lifecycleOwner) {
            _notesList.value = it
        }
    }


}