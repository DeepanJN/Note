package com.deejayen.note.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Note(
    @PrimaryKey var noteId: Long? = null,
    var title: String? = null,
)