package com.deejayen.note.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.deejayen.note.database.NoteDatabase
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.database.entity.NoteTextDetail
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class NoteRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var noteDao: NoteDao
    private lateinit var database: NoteDatabase

    companion object {
        const val TEST_TITLE = "TEST_TITLE"
        const val TEST_DESCRIPTION = "TEST_DESCRIPTION"
    }

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        noteDao = database.noteDao
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testBasicInsertForAllTables() = runTest {

        var noteId = 0L
        launch {
            noteId = noteDao.insertNote(Note(title = "Title"))
            noteDao.insertNoteTextDetail(NoteTextDetail(value = "Detail", noteId = noteId))
            noteDao.insertImageTextDetail(NoteImageDetail(value = "filepath/", noteId = noteId))
        }.join()

        val noteDetail = noteDao.getNoteWithDetailsByNoteId(noteId)
        assertEquals(noteDetail?.note?.title ?: "", "Title")
        assertEquals(noteDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", "Detail")
        assertEquals(noteDetail?.noteImageDetailList?.firstOrNull()?.value ?: "", "filepath/")
    }


    @Test
    fun checkInsertOrUpdateNoteWithDetail() = runTest {

        val noteWithDetail = NoteWithDetail()
        noteWithDetail.note = Note(title = TEST_TITLE)
        noteWithDetail.noteTextDetailList = arrayListOf(NoteTextDetail(value = TEST_DESCRIPTION))

        launch {
            noteDao.insertOrUpdateNoteWithDetail(noteWithDetail)
        }.join()

        val noteId = noteWithDetail.note?.noteId

        val updatedNoteDetail = noteId?.let { noteDao.getNoteWithDetailsByNoteId(it) }
        assertEquals(updatedNoteDetail?.note?.title ?: "", TEST_TITLE)
        assertEquals(updatedNoteDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", TEST_DESCRIPTION)

    }


    @Test
    fun checkInsertNote() = runTest {

        var noteId = 0L
        launch {
            noteId = noteDao.insertNote(Note(title = TEST_TITLE))
            noteDao.insertNoteTextDetail(NoteTextDetail(value = "Detail", noteId = noteId))
            /*noteDao.insertImageTextDetail(NoteImageDetail(value = "filepath", noteId = noteId))*/
        }.join()

        val noteDetail = noteDao.getNoteWithDetailsByNoteId(noteId)
        assertEquals(noteDetail?.note?.title ?: "", "Title")
        assertEquals(noteDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", "Title")

    }

    @Test
    fun testInsertNoteAndTextDetail() = runTest {

        var noteId = 0L
        launch {
            val note = Note(title = TEST_TITLE)
            noteId = noteDao.insertNote(note)
            val noteTextDetail = NoteTextDetail(value = TEST_DESCRIPTION, noteId = noteId)
            val noteWithDetail = NoteWithDetail(note, listOf(noteTextDetail), listOf())
            noteDao.insertOrUpdateNoteWithDetail(noteWithDetail)
        }.join()


        var updatedNoteWithDetail:NoteWithDetail? = null
        launch {
            updatedNoteWithDetail = noteDao.getNoteWithDetailsByNoteId(noteId)
        }.join()

        assertEquals(updatedNoteWithDetail?.note?.title ?: "", TEST_TITLE)
        assertEquals(updatedNoteWithDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", TEST_DESCRIPTION)


    }

}
