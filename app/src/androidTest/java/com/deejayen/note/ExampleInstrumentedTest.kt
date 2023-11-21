package com.deejayen.note

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deejayen.note.database.NoteDatabase
import com.deejayen.note.database.dao.NoteDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var noteDao: NoteDao
    private lateinit var database: NoteDatabase

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
    fun insertNoteWithDetails() = runTest{
        launch {

//            noteDao.insertNoteWithDetails()
        }
    }

//    @Test
//    fun insertAndReadNote() = runTest {
//        // Insert
//        val note = Note(title = "Test Note")
//
//        var noteId: Long = 0L
//        launch {
//            noteId = noteDao.insertNote(note)
//            val noteDetail = NoteDetail(noteId= noteId, type = NoteType.TEXT,  value ="This is a test value")
//            noteDao.insertNoteDetail(noteDetail)
//        }.join()
//
//        // Read
//        var loadedNote: NoteWithDetail? = null
//        launch {
//            loadedNote = noteDao.getNoteWithContentById(noteId.toInt())
//        }.join()
//
//        assertNotNull(loadedNote)
//        assertEquals("Test Note", loadedNote?.note?.title ?: "")
//        assertEquals("This is a test value", loadedNote?.noteDetailList?.firstOrNull()?.value ?: "")
//        assertEquals( NoteType.TEXT, loadedNote?.noteDetailList?.firstOrNull()?.type ?: "")
//    }
//
//    @Test
//    fun insertAndUpdateNoteContent() = runTest {
//
//        val oldContentStr = "Content"
//        val updatedContentStr = "Updated Content"
//
//        val note = Note(title = "Test Note")
//        val noteId = noteDao.insertNote(note)
//
//        val noteDetail = NoteDetail(noteId = noteId, type = NoteType.TEXT, value = oldContentStr)
//        noteDao.insertNoteDetail(noteDetail)
//
////        val updatedContent = noteDao.getAllNoteWithDetails(). ?.firstOrNull()?.noteDetailList?.firstOrNull()?.copy(value = updatedContentStr)
////        updatedContent?.let { noteDao.updateNoteContent(it) }
////
////        val loadedContent = noteDao.getAllNotes()?.firstOrNull()?.noteDetailList?.firstOrNull()
////        assertEquals(updatedContentStr, loadedContent?.value ?: "")
//    }
//
//    @Test
//    fun insertAndDeleteNoteContent() = runTest {
//        // Insert
//        val note = Note(title = "Test Note")
//        val noteId = noteDao.insertNote(note)
//
//        val noteDetail = NoteDetail(noteId = noteId, type = NoteType.TEXT, value = "Content")
//        noteDao.insertNoteDetail(noteDetail)
//
//        // Delete
//        launch {
//            val contentToDelete = noteDao.getAllNoteWithDetails().firstOrNull()?.noteDetailList?.firstOrNull()
//            contentToDelete?.let { noteDao.deleteNoteDetail(it) }
//        }.join()
//
//        // Assert
//        var loadedContent:List<NoteWithDetail>? = null
//        launch {
//            loadedContent = noteDao.getAllNoteWithDetails()
//        }.join()
//
//        assertTrue(loadedContent?.isEmpty() ?: false)
//    }

}
