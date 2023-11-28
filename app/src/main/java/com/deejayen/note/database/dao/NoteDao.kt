package com.deejayen.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.database.entity.NoteTextDetail

@Dao
interface NoteDao {

    //region Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteTextDetail(noteTextDetail: NoteTextDetail): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageTextDetail(noteImageDetail: NoteImageDetail): Long
    //endregion

    //region Read

    @Transaction
    @Query("SELECT * FROM Note")
    fun getAllNote(): LiveData<List<Note>>

    @Transaction
    @Query("SELECT * FROM NoteImageDetail LEFT JOIN Note on Note.noteId = NoteImageDetail.noteId WHERE Note.noteId = :noteId ")
    fun getImageDetailsForNoteId(noteId: Long): LiveData<List<NoteImageDetail>>

    @Transaction
    @Query("SELECT * FROM NoteImageDetail WHERE NoteImageDetail.noteImageDetailId = :noteImageDetailId ")
    suspend fun getImageDetailForImageDetailId(noteImageDetailId: Long): NoteImageDetail?

    @Query("SELECT * FROM Note WHERE Note.noteId = :noteId ")
    suspend fun getNoteWithDetailsByNoteId(noteId: Long): NoteWithDetail?

    //endregion

    //region Update
    @Update
    suspend fun updateNote(note: Note)

    @Update
    suspend fun updateNoteTextDetail(noteTextDetail: NoteTextDetail)

    @Update
    suspend fun updateNoteImageDetail(noteImageDetail: NoteImageDetail)
    //endregion

    //region Delete
    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteTextDetail(noteTextDetail: NoteTextDetail)

    @Delete
    suspend fun deleteNoteImageDetail(noteImageDetail: NoteImageDetail)

    //endregion
    @Transaction
    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        val note = noteWithDetail.note
        val noteDetails = noteWithDetail.noteTextDetailList.firstOrNull()
        note?.let { deleteNote(it) }
    }

    @Transaction
    suspend fun insertOrUpdateNoteWithDetailList(noteWithDetailList: ArrayList<NoteWithDetail>): ArrayList<NoteWithDetail> {
        noteWithDetailList.forEach {
            insertOrUpdateNoteWithDetail(it)
        }
        return noteWithDetailList
    }

    @Transaction
    suspend fun insertOrUpdateNoteWithDetail(noteWithDetail: NoteWithDetail): NoteWithDetail {

        val note = noteWithDetail.note ?: return noteWithDetail
        val noteTextDetail = noteWithDetail.noteTextDetailList.firstOrNull()
        val noteImageDetailList = noteWithDetail.noteImageDetailList

        val noteId = note.noteId
        if (noteId != 0L) { // Update
            updateNote(note)
            insertOrUpdateNoteTextDetail(noteTextDetail, noteId)
            noteImageDetailList.forEach { noteImageDetail ->
                insertOrUpdateNoteImageDetail(noteImageDetail, noteId)
            }
        } else {
            val newNoteId = insertNote(note)
            note.noteId = newNoteId
            insertOrUpdateNoteTextDetail(noteTextDetail, newNoteId)
            noteImageDetailList.forEach { noteImageDetail ->
                insertOrUpdateNoteImageDetail(noteImageDetail, newNoteId)
            }
        }
        return noteWithDetail
    }

    @Transaction
    suspend fun insertOrUpdateNoteImageDetail(noteImageDetail: NoteImageDetail, noteId: Long) {
        noteImageDetail.noteId = noteId
        if (noteImageDetail.noteImageDetailId != 0L) {
            updateNoteImageDetail(noteImageDetail)
        } else {
            val newNoteImageDetailId = insertImageTextDetail(noteImageDetail)
            noteImageDetail.noteImageDetailId = newNoteImageDetailId
        }
    }

    @Transaction
    suspend fun insertOrUpdateNoteTextDetail(noteTextDetail: NoteTextDetail?, noteId: Long): NoteTextDetail? {
        noteTextDetail ?: return null
        val noteTextDetailId = noteTextDetail.noteTextDetailId
        noteTextDetail.noteId = noteId
        if (noteTextDetailId != 0L) {
            updateNoteTextDetail(noteTextDetail)
        } else {
            val newNoteTextDetailId = insertNoteTextDetail(noteTextDetail)
            noteTextDetail.noteTextDetailId = newNoteTextDetailId
        }
        return noteTextDetail
    }


}
