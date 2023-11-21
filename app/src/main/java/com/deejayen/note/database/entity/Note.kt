package com.deejayen.note.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var noteId: Long = 0L,
    var title: String? = null,
)