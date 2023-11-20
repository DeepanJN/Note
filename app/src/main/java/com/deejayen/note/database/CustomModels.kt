package com.deejayen.note.database

import androidx.room.Embedded
import androidx.room.Relation
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteContent

class CustomModels {

    data class NoteWithContent(
        @Embedded val note: Note,
        @Relation(parentColumn = "noteId", entityColumn = "noteId")
        val noteDetails: List<NoteContent>,
    )
}
