package com.deejayen.note.ui.noteDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteDetailViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    var noteId = 0L
    private var _selectedNoteWithDetail = MutableLiveData<NoteWithDetail>()
    val selectedNoteWithDetail: LiveData<NoteWithDetail> get() = _selectedNoteWithDetail

    val ON_TYPE_DELAY: Long = 600L

    suspend fun insertOrUpdateNoteWithDetailList(noteWithDetail: NoteWithDetail?) {
        withContext(Dispatchers.IO) {
            noteWithDetail?.let {
                _selectedNoteWithDetail.postValue(noteRepository.insertOrUpdateNoteWithDetail(it))
            }
        }
    }

    suspend fun getNoteWithDetailsByNoteId() {
        withContext(Dispatchers.IO) {
            _selectedNoteWithDetail.postValue(noteRepository.getNoteWithDetailsByNoteId(noteId))
        }
    }

    suspend fun saveImageFileToContent(vararg imageFilePath: String) {
        withContext(Dispatchers.IO) {
            val currentNoteWithDetail = selectedNoteWithDetail.value ?: NoteWithDetail(Note())
            val noteImageDetailList = currentNoteWithDetail.noteImageDetailList.toMutableList()
            val noteId = currentNoteWithDetail.note?.noteId ?: 0L
            imageFilePath.forEach {
                val noteDetail = NoteImageDetail(value = it, noteId = noteId)
                noteImageDetailList.add(noteDetail)
            }
            currentNoteWithDetail.noteImageDetailList = noteImageDetailList
            insertOrUpdateNoteWithDetailList(currentNoteWithDetail)
        }
    }


    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(note)
        }
    }

}
