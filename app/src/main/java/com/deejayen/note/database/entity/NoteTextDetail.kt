package com.deejayen.note.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//enum class NoteType {
//    TEXT, IMAGE, NOT_DEFINED
//}

@Entity(
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = ["noteId"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class NoteTextDetail(
    @PrimaryKey(autoGenerate = true) var noteTextDetailId: Long = 0L,
    var noteId: Long = 0L,
    var value: String? = null,
)

