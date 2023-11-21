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

data class NoteDetail(
    @PrimaryKey var noteDetailId: Long? = null,
    var noteId: Long? = null,
    var type: NoteType? = null,
    var value: String? = null,
)

