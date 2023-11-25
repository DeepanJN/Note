package com.deejayen.note.repository

import androidx.lifecycle.LiveData
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(private val noteDao: NoteDao) {
//    fun getAllNoteWithDetail(): LiveData<List<NoteWithDetail>> {
//        return noteDao.getAllNoteWithDetail()
//    }

    fun getAllNote(): LiveData<List<Note>> {
        return noteDao.getAllNote()
    }

    suspend fun insertOrUpdateNoteWithDetail(noteWithDetail: NoteWithDetail): NoteWithDetail {
        return noteDao.insertOrUpdateNoteWithDetail(noteWithDetail)
    }

    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        return noteDao.deleteNoteWithDetail(noteWithDetail)
    }

    suspend fun getNoteWithDetailsByNoteId(noteId: Long): NoteWithDetail? {
        return noteDao.getNoteWithDetailsByNoteId(noteId)
    }

    suspend fun deleteNote(note: Note) {
        return noteDao.deleteNote(note)
    }

    suspend fun deleteNoteImageDetail(noteImageDetail: NoteImageDetail) {
        withContext(Dispatchers.IO) {
            noteImageDetail.value?.let { filePath ->
                val file = File(filePath)
                if (file.exists()) {
                    file.delete()
                }
            }
            noteDao.deleteNoteImageDetail(noteImageDetail)
        }
    }


}
