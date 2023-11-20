package com.deejayen.note.database.dao

import androidx.room.*
import com.deejayen.note.database.NoteWithContent
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteContent

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteContent(noteContent: NoteContent): Long

    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteContent ON Note.noteId = NoteContent.noteId")
    suspend fun getAllNotesWithContent(): List<NoteWithContent>

    @Transaction
    @Query("SELECT * FROM Note INNER JOIN NoteContent ON Note.noteId = NoteContent.noteId WHERE Note.noteId = :noteId")
    suspend fun getNoteWithContentById(noteId: Int): NoteWithContent

    @Update
    suspend fun updateNoteContent(noteContent: NoteContent)

    @Delete
    suspend fun deleteNoteContent(noteContent: NoteContent)
}
