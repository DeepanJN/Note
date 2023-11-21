package com.deejayen.note.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.deejayen.note.database.converter.NoteTypeConverter

enum class NoteType {
    TEXT, IMAGE, NOT_DEFINED
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
    @PrimaryKey(autoGenerate = true) var noteDetailId: Long = 0L,
    var noteId: Long = 0L,
    var type: NoteType = NoteType.NOT_DEFINED,
    var value: String? = null,
)

