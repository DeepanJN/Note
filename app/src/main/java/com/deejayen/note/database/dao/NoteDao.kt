package com.deejayen.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteTextDetail

@Dao
interface NoteDao {

    //region Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteTextDetail(noteTextDetail: NoteTextDetail): Long
    //endregion

    //region Read
    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteTextDetail ON Note.noteId = NoteTextDetail.noteId GROUP BY Note.noteId")
    fun getAllNoteWithDetail(): LiveData<List<NoteWithDetail>>

    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteTextDetail ON Note.noteId = NoteTextDetail.noteId WHERE Note.noteId = :noteId")
    suspend fun getNoteWithDetailsByNoteId(noteId: Long): NoteWithDetail
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
    //endregion

    //Delete image files before deleting the entry
    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        val note = noteWithDetail.note
        val noteDetails = noteWithDetail.noteTextDetailList.firstOrNull()
        deleteNote(note)
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

        val note = noteWithDetail.note
        val noteTextDetailList = noteWithDetail.noteTextDetailList

        val noteId = note.noteId
        if (noteId != 0L) { // Update

            val localNoteWithDetail = getNoteWithDetailsByNoteId(noteId)
            val localNoteTextDetailList = localNoteWithDetail.noteTextDetailList

            updateNote(note)

            noteTextDetailList.forEachIndexed { index, noteTextDetail ->

                val localTextDetail = localNoteTextDetailList.firstOrNull()
                if (localTextDetail?.noteTextDetailId == noteTextDetail.noteTextDetailId) {
                    updateNoteTextDetail(noteTextDetail)
                } else {
                    noteTextDetail.noteId = noteId
                    insertNoteTextDetail(noteTextDetail)
                }

            }

//            localNoteTextDetailList.forEachIndexed { index, localNoteDetail ->
//                var hasLocalEntry = false
//                noteTextDetailList.forEach { noteDetail ->
//                    if (noteDetail.noteTextDetailId == localNoteDetail.noteTextDetailId) {
//                        hasLocalEntry = true
//                        return@forEach
//                    }
//                }
//                if (!hasLocalEntry) {
//                    deleteNoteTextDetail(localNoteDetail) //Delete the image before accessing dao
//                }
//            }

        } else { // Insert
            val newNoteId = insertNote(note)
            note.noteId = newNoteId
            val noteTextDetail = noteTextDetailList.firstOrNull()

            noteTextDetail?.let {
                noteTextDetail.noteId = newNoteId
                insertNoteTextDetail(noteTextDetail)
            }

        }

        return noteWithDetail

    }

}
