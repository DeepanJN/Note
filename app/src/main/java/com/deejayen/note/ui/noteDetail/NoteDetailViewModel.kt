package com.deejayen.note.ui.noteDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class NoteDetailViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private var _selectedNoteWithDetail = MutableLiveData<NoteWithDetail>()
    val selectedNoteWithDetail: LiveData<NoteWithDetail> get() = _selectedNoteWithDetail

    private var _headingTextUpdateJob: Job? = null
    private var _contentTextUpdateJob: Job? = null

    val onTypeDelay: Long = 5000 // 5 seconds

    suspend fun insertOrUpdateNoteWithDetailList(noteWithDetail: NoteWithDetail?) {
        withContext(Dispatchers.IO) {
            noteWithDetail?.let {
                _selectedNoteWithDetail.postValue(noteRepository.insertOrUpdateNoteWithDetail(it))
            }
        }
    }

    suspend fun getNoteWithDetailsByNoteId(noteId: Long) {
        withContext(Dispatchers.IO) {
            _selectedNoteWithDetail.postValue(noteRepository.getNoteWithDetailsByNoteId(noteId))
        }
    }

    fun setHeadingTextUpdateJob(job: Job) {
        _headingTextUpdateJob = job
    }

    fun cancelHeadingTextUpdateJob() {
        _headingTextUpdateJob?.cancel()
    }

    fun setContentTextUpdateJob(job: Job) {
        _contentTextUpdateJob = job
    }

    fun cancelContentTextUpdateJob() {
        _contentTextUpdateJob?.cancel()
    }
}
