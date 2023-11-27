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
        const val TITLE = "Title of the note"
        const val UPDATED_TITLE = "updated Title of a note"
        const val DESCRIPTION = "This is a note description"
        const val UPDATED_DESCRIPTION = "Updated note description"
        const val FILE_PATH_ONE = "data/user/first_image.png"
        const val FILE_PATH_TWO = "data/user/second_image.png"
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
    fun testInsertOrUpdateNoteWithImageDetail() = runTest {

        var noteWithDetail: NoteWithDetail? = null
        launch {
            val noteTextDetail = listOf(NoteTextDetail(value = DESCRIPTION))
            val noteImageDetailList = listOf(NoteImageDetail(value = FILE_PATH_ONE), NoteImageDetail(value = FILE_PATH_TWO))
            noteWithDetail = NoteWithDetail(Note(title = TITLE), noteTextDetail, noteImageDetailList)
            noteDao.insertOrUpdateNoteWithDetail(noteWithDetail!!)
        }.join()

        noteWithDetail = noteDao.getNoteWithDetailsByNoteId(noteWithDetail?.note?.noteId ?: 0L)
        assertEquals(noteWithDetail?.note?.title ?: "", TITLE)
        assertEquals(noteWithDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", DESCRIPTION)
        assertEquals(noteWithDetail?.noteImageDetailList?.firstOrNull()?.value ?: "", FILE_PATH_ONE)
        assertEquals(noteWithDetail?.noteImageDetailList?.lastOrNull()?.value ?: "", FILE_PATH_TWO)

        //Update
        noteWithDetail?.note?.title = UPDATED_TITLE
        noteWithDetail?.noteTextDetailList?.firstOrNull()?.value = UPDATED_DESCRIPTION

        launch {
            noteWithDetail = noteDao.insertOrUpdateNoteWithDetail(noteWithDetail!!)
        }.join()

        assertEquals(noteWithDetail?.note?.title ?: "", UPDATED_TITLE)
        assertEquals(noteWithDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", UPDATED_DESCRIPTION)

    }

    @Test
    fun testInsertNoteAndTextDetail() = runTest {

        var noteId = 0L
        launch {
            val note = Note(title = TITLE)
            val noteTextDetail = NoteTextDetail(value = DESCRIPTION)
            var noteWithDetail = NoteWithDetail(note, listOf(noteTextDetail), listOf())
            noteWithDetail = noteDao.insertOrUpdateNoteWithDetail(noteWithDetail)
            noteId = noteWithDetail.note?.noteId ?: 0L
        }.join()

        var updatedNoteWithDetail: NoteWithDetail? = null
        launch {
            updatedNoteWithDetail = noteDao.getNoteWithDetailsByNoteId(noteId)
        }.join()

        assertEquals(updatedNoteWithDetail?.note?.title ?: "", TITLE)
        assertEquals(updatedNoteWithDetail?.noteTextDetailList?.firstOrNull()?.value ?: "", DESCRIPTION)

    }

    @Test
    fun testInsertNoteAndImageDetail() = runTest {

        var noteId = 0L
        launch {
            val note = Note(title = TITLE)
            val noteImageDetailList = arrayListOf(NoteImageDetail(value = FILE_PATH_ONE), NoteImageDetail(value = FILE_PATH_TWO))
            var noteWithDetail = NoteWithDetail(note, listOf(), noteImageDetailList)
            noteWithDetail = noteDao.insertOrUpdateNoteWithDetail(noteWithDetail)
            noteId = noteWithDetail.note?.noteId ?: 0L
        }.join()

        var updatedNoteWithDetail: NoteWithDetail? = null
        launch {
            updatedNoteWithDetail = noteDao.getNoteWithDetailsByNoteId(noteId)
        }.join()

        assertEquals(updatedNoteWithDetail?.note?.title ?: "", TITLE)
        assertEquals(updatedNoteWithDetail?.noteImageDetailList?.firstOrNull()?.value ?: "", FILE_PATH_ONE)
        assertEquals(updatedNoteWithDetail?.noteImageDetailList?.lastOrNull()?.value ?: "", FILE_PATH_TWO)


    }


}
