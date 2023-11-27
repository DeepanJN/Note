package com.deejayen.note.database

import androidx.room.Embedded
import androidx.room.Relation
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.database.entity.NoteTextDetail


data class NoteWithDetail(

    @Embedded var note: Note? = null,

    @Relation(parentColumn = "noteId", entityColumn = "noteId")
    var noteTextDetailList: List<NoteTextDetail> = emptyList(),

    @Relation(parentColumn = "noteId", entityColumn = "noteId")
    var noteImageDetailList: List<NoteImageDetail> = emptyList(),

    )

