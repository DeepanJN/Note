package com.deejayen.note.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = ["noteId"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class NoteImageDetail(
    @PrimaryKey(autoGenerate = true) var noteImageDetailId: Long = 0L,
    var noteId: Long = 0L,
    var value: String? = null,
)

