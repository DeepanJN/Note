package com.deejayen.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail

@Dao
interface NoteDao {

    //region Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteDetail(noteDetail: NoteDetail): Long
    //endregion

    //region Read
    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteDetail ON Note.noteId = NoteDetail.noteId GROUP BY Note.noteId")
    fun getAllNoteWithDetail(): LiveData<List<NoteWithDetail>>

    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteDetail ON Note.noteId = NoteDetail.noteId WHERE Note.noteId = :noteId")
    suspend fun getNoteWithDetailsByNoteId(noteId: Long): NoteWithDetail
    //endregion

    //region Update
    @Update
    suspend fun updateNote(note: Note)

    @Update
    suspend fun updateNoteDetail(noteDetail: NoteDetail)
    //endregion

    //region Delete
    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteDetail(noteDetail: NoteDetail)
    //endregion

    //Delete image files before deleting the entry
    suspend fun deleteNoteWithDetail(noteWithDetail: NoteWithDetail) {
        val note = noteWithDetail.note
        val noteDetails = noteWithDetail.noteDetailList
        deleteNote(note)
        noteDetails.forEach {
            deleteNoteDetail(it)
        }
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
        val noteDetailList = noteWithDetail.noteDetailList

        val noteId = note.noteId
        if (noteId != 0L) { // Update

            val localNoteWithDetail = getNoteWithDetailsByNoteId(noteId)
            val localNoteDetailList = localNoteWithDetail.noteDetailList

            updateNote(note)

            noteDetailList.forEachIndexed { index, noteDetail ->
                var hasLocalEntry = false
                localNoteDetailList.forEach { localNoteDetail ->
                    if (localNoteDetail.noteDetailId == noteDetail.noteDetailId) {
                        hasLocalEntry = true
                        return@forEach
                    }
                }
                if (hasLocalEntry) {
                    updateNoteDetail(noteDetail)
                } else {
                    noteDetail.noteId = noteId
                    insertNoteDetail(noteDetail)
                }

            }

            localNoteDetailList.forEachIndexed { index, localNoteDetail ->
                var hasLocalEntry = false
                noteDetailList.forEach { noteDetail ->
                    if (noteDetail.noteDetailId == localNoteDetail.noteDetailId) {
                        hasLocalEntry = true
                        return@forEach
                    }
                }
                if (!hasLocalEntry) {
                    deleteNoteDetail(localNoteDetail) //Delete the image before accessing dao
                }
            }

        } else { // Insert
            val newNoteId = insertNote(note)
            note.noteId = newNoteId
            noteDetailList.forEach {
                it.noteId = newNoteId
                insertNoteDetail(it)
            }

        }

        return noteWithDetail


    }


}
