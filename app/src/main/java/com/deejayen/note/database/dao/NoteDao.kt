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

//    @Transaction
//    @Query("SELECT * FROM Note INNER JOIN NoteTextDetail ON Note.noteId = NoteTextDetail.noteId GROUP BY Note.noteId")
//    fun getAllNoteWithDetail(): LiveData<List<NoteWithDetail>>

    @Query("SELECT * FROM Note WHERE Note.noteId = :noteId")
    fun getNoteWithDetailsByNoteId(noteId: Long): NoteWithDetail?

    //endregion

    //region Update
    @Update
    suspend fun updateNote(note: Note)

    @Update
    suspend fun updateNoteTextDetail(noteTextDetail: NoteTextDetail)
    //endregion

    //region Delete
    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteTextDetail(noteTextDetail: NoteTextDetail)

    @Delete
    suspend fun deleteNoteImageDetail(noteImageDetail: NoteImageDetail)

    //endregion

    //Delete image files before deleting the entry
    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        val note = noteWithDetail.note
        val noteDetails = noteWithDetail.noteTextDetailList.firstOrNull()
        note?.let { deleteNote(it) }
        //TODO:Check cascade delete working properly
    }

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

        } else {

            // Insert
            val newNoteId = insertNote(note)
            note.noteId = newNoteId
            insertOrUpdateNoteTextDetail(noteTextDetail, newNoteId)

//            noteImageDetailList.forEach {
//                it.noteId = newNoteId
//                insertImageTextDetail(it)
//            }
        }
        return noteWithDetail

    }

    @Transaction
    suspend fun insertOrUpdateNoteTextDetail(noteTextDetail: NoteTextDetail?, noteId: Long) {
        noteTextDetail ?: return
        val noteTextDetailId = noteTextDetail.noteTextDetailId
        noteTextDetail.noteId = noteId
        if (noteTextDetailId != 0L) {
            updateNoteTextDetail(noteTextDetail)
        } else {
            val newNoteTextDetailId = insertNoteTextDetail(noteTextDetail)
            noteTextDetail.noteTextDetailId = newNoteTextDetailId
        }
    }


}
