package com.deejayen.note.ui.noteList

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.deejayen.note.database.entity.Note
import com.deejayen.note.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    val notesList: MediatorLiveData<List<Note>> = MediatorLiveData()

    fun getAllNote() {
        notesList.addSource(noteRepository.getAllNote()) {
            notesList.value = it
        }
    }

    suspend fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)

    }

}