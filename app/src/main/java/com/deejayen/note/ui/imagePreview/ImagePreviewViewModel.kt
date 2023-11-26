package com.deejayen.note.ui.imagePreview

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagePreviewViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    var noteImageDetailId: Long = 0L
    var noteImageDetail: NoteImageDetail? = null

//    private var _noteImageDetailList = MutableLiveData<ArrayList<NoteImageDetail>>()
//    val noteImageDetailList: LiveData<ArrayList<NoteImageDetail>> get() = _noteImageDetailList

    fun deleteNoteImageDetail(noteImageDetail: NoteImageDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNoteImageDetail(noteImageDetail)
        }
    }

//    fun getImageDetailsForNoteId(lifecycleOwner: LifecycleOwner) {
//        noteRepository.getImageDetailsForNoteId(noteId).observe(lifecycleOwner) {
//            _noteImageDetailList.value = it as ArrayList<NoteImageDetail>
//        }
//    }

    suspend fun getImageDetailForImageDetailId(): NoteImageDetail? {
        noteImageDetail = noteRepository.getImageDetailForImageDetailId(noteImageDetailId)
        return noteImageDetail
    }


}