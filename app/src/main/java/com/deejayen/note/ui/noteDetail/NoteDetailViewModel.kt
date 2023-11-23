package com.deejayen.note.ui.noteDetail

import androidx.lifecycle.ViewModel
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.repository.NoteRepository
import kotlinx.coroutines.Job

class NoteDetailViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    var selectedNoteWithDetail: NoteWithDetail? = null
    var headingTextUpdateJob: Job? = null
    var contentTextUpdateJob: Job? = null
    val onTypeDelay: Long = 5000 // 5 seconds

    suspend fun insertOrUpdateNoteWithDetailList(noteWithDetail: NoteWithDetail?): NoteWithDetail? {
        noteWithDetail?.let {
            selectedNoteWithDetail = noteRepository.insertOrUpdateNoteWithDetail(it)
        }
        return selectedNoteWithDetail
    }

    suspend fun getNoteWithDetailsByNoteId(noteId: Long): NoteWithDetail? {
        selectedNoteWithDetail = noteRepository.getNoteWithDetailsByNoteId(noteId)
        return selectedNoteWithDetail
    }


}