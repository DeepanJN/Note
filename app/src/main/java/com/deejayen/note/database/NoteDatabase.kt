package com.deejayen.note.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.database.entity.NoteTextDetail

@Database(
    entities = [Note::class, NoteTextDetail::class, NoteImageDetail::class],
    version = 1
)

abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "note_db"
    }
}