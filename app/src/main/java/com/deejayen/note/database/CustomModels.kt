package com.deejayen.note.database

import androidx.room.Embedded
import androidx.room.Relation
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.database.entity.NoteTextDetail


data class NoteWithDetail(

    @Embedded val note: Note,

    @Relation(parentColumn = "noteId", entityColumn = "noteId")
    val noteTextDetailList: List<NoteTextDetail>, //One

    @Relation(parentColumn = "noteId", entityColumn = "noteId")
    val noteImageDetailList: List<NoteImageDetail>, //Many

)

