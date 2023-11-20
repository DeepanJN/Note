package com.deejayen.note.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.deejayen.note.database.converter.NoteTypeConverter

enum class NoteType {
    TEXT, IMAGE,
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = ["noteId"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(NoteTypeConverter::class)

data class NoteContent(
    @PrimaryKey val noteDetailId: Long? = null,
    val noteId: Long,
    val type: NoteType,
    val value: String,
)

