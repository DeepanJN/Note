package com.deejayen.note.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Note(
    @PrimaryKey val noteId: Long? = null,
    val title: String,
)