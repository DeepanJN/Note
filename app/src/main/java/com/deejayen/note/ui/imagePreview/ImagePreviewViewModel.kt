package com.deejayen.note.ui.imagePreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    var noteImageDetailId: Long = 0L
    var noteImageDetail: NoteImageDetail? = null

    fun deleteNoteImageDetail(noteImageDetail: NoteImageDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNoteImageDetail(noteImageDetail)
        }
    }

    suspend fun getImageDetailForImageDetailId(): NoteImageDetail? {
        noteImageDetail = noteRepository.getImageDetailForImageDetailId(noteImageDetailId)
        return noteImageDetail
    }


}