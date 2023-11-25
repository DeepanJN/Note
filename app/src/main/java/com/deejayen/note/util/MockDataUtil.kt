package com.deejayen.note.util

import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteTextDetail


class MockDataUtil {
    companion object {
        fun getMockNoteWithDetail(): NoteWithDetail {
            val note = Note(title = "My Trip")
            val noteDetailArrayList = arrayListOf<NoteTextDetail>(
                NoteTextDetail(value = "This is new place"),
                NoteTextDetail(value = "image/path"),
                NoteTextDetail(value = "This is line 2"),
                NoteTextDetail(value = "image/path"),
                NoteTextDetail(value = "This is line 3")
            )
            return NoteWithDetail(note, noteDetailArrayList, listOf())
        }
    }
}

