package com.deejayen.note.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteContent
import com.deejayen.note.database.converter.NoteTypeConverter

@Database(
    entities = [Note::class, NoteContent::class],
    version = 1
)
@TypeConverters(NoteTypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "note_db"
    }
}