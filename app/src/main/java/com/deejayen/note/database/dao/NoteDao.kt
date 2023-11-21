package com.deejayen.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.deejayen.note.database.NoteWithDetails
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteDetail(noteDetail: NoteDetail): Long

    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteDetail ON Note.noteId = NoteDetail.noteId")
    fun getAllNotesWithContent(): LiveData<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteDetail ON Note.noteId = NoteDetail.noteId WHERE Note.noteId = :noteId")
    suspend fun getNoteWithContentById(noteId: Int): NoteWithDetails

    @Update
    suspend fun updateNote(note: Note)

    @Update
    suspend fun updateNoteDetail(noteDetail: NoteDetail)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteDetail(noteDetail: NoteDetail)
}
