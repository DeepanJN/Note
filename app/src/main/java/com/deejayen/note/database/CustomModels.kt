package com.deejayen.note.database

import androidx.room.Embedded
import androidx.room.Relation
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail


data class NoteWithDetails(

    @Embedded val note: Note,

    @Relation(parentColumn = "noteId", entityColumn = "noteId")
    val noteDetailList: List<NoteDetail>,

    )

