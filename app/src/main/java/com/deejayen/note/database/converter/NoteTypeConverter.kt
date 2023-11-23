package com.deejayen.note.database.converter
import androidx.room.TypeConverter
import com.deejayen.note.database.entity.NoteType

class NoteTypeConverter {
    @TypeConverter
    fun fromNoteType(value: NoteType): String {
        return value.name
    }

    @TypeConverter
    fun toNoteType(value: String): NoteType {
        return enumValueOf(value)
    }

}