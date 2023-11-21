package com.deejayen.note.repository

import androidx.lifecycle.LiveData
import com.deejayen.note.database.NoteWithDetails
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(private val noteDao: NoteDao) {
    fun getAllNotes(): LiveData<List<NoteWithDetails>> {
        return noteDao.getAllNotesWithContent()
    }

//    fun addOrUpdateNote(noteWithDetailsArrList: ArrayList<NoteWithDetails>) {
//
//        val a = Note(title = "test")
//        a.noteId = 1
//
//        noteWithDetailsArrList.forEach { noteWithDetails ->
//            val note = noteWithDetails.note
//            val noteDetails = noteWithDetails.noteDetailList
//            if (note.noteId != null){
//
//            }
//        }
//    }

    private suspend fun updateNote(noteWithDetails: NoteWithDetails) {
        val note = noteWithDetails.note
        val noteDetails = noteWithDetails.noteDetailList
        noteDao.updateNote(note)
        noteDetails.forEach {
            noteDao.updateNoteDetail(it)
        }

    }

    suspend fun deleteNote(noteWithDetails: NoteWithDetails) {
        val note = noteWithDetails.note
        val noteDetails = noteWithDetails.noteDetailList
        noteDao.deleteNote(note)
        noteDetails.forEach {
            noteDao.deleteNoteDetail(it)
        }
    }

}
