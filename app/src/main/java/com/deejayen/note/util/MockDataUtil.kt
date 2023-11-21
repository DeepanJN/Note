package com.deejayen.note.util

import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail
import com.deejayen.note.database.entity.NoteType


class MockDataUtil {
    companion object {
        fun getMockNoteWithDetail(): NoteWithDetail {
            val note = Note(title = "My Trip")
            val noteDetailArrayList = arrayListOf<NoteDetail>(
                NoteDetail(value = "This is new place", type = NoteType.TEXT),
                NoteDetail(value = "image/path", type = NoteType.IMAGE),
                NoteDetail(value = "This is line 2", type = NoteType.TEXT),
                NoteDetail(value = "image/path", type = NoteType.IMAGE),
                NoteDetail(value = "This is line 3", type = NoteType.TEXT)
            )
            return NoteWithDetail(note, noteDetailArrayList)
        }
    }
}

