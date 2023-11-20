package com.deejayen.note

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deejayen.note.database.NoteDatabase
import com.deejayen.note.database.NoteWithContent
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteContent
import com.deejayen.note.database.entity.NoteType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runners.JUnit4

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
    fun insertAndReadNote() = runBlocking {
        // Insert
        val note = Note(title = "Test Note")

        var noteId: Long = 0L
        launch {
            noteId = noteDao.insertNote(note)
            val noteContent = NoteContent(noteId= noteId, type = NoteType.TEXT,  value ="This is a test value")
            noteDao.insertNoteContent(noteContent)
        }.join()

        // Read
        var loadedNote: NoteWithContent? = null
        launch {
            loadedNote = noteDao.getNoteWithContentById(noteId.toInt())
        }.join()

        assertNotNull(loadedNote)
        assertEquals("Test Note", loadedNote?.note?.title ?: "")
        assertEquals("This is a test value", loadedNote?.noteDetails?.firstOrNull()?.value ?: "")
        assertEquals( NoteType.TEXT, loadedNote?.noteDetails?.firstOrNull()?.type ?: "")
    }

    @Test
    fun insertAndUpdateNoteContent() = runBlocking {

        val oldContentStr = "Content"
        val updatedContentStr = "Updated Content"

        val note = Note(title = "Test Note")
        val noteId = noteDao.insertNote(note)

        val noteContent = NoteContent(noteId = noteId, type = NoteType.TEXT, value = oldContentStr)
        noteDao.insertNoteContent(noteContent)

        val updatedContent = noteDao.getAllNotesWithContent()?.firstOrNull()?.noteDetails?.firstOrNull()?.copy(value = updatedContentStr)
        updatedContent?.let { noteDao.updateNoteContent(it) }

        val loadedContent = noteDao.getAllNotesWithContent()?.firstOrNull()?.noteDetails?.firstOrNull()
        assertEquals(updatedContentStr, loadedContent?.value ?: "")
    }

    @Test
    fun insertAndDeleteNoteContent() = runBlocking {
        // Insert
        val note = Note(title = "Test Note")
        val noteId = noteDao.insertNote(note)

        val noteContent = NoteContent(noteId = noteId, type = NoteType.TEXT, value = "Content")
        noteDao.insertNoteContent(noteContent)

        // Delete
        launch {
            val contentToDelete = noteDao.getAllNotesWithContent().firstOrNull()?.noteDetails?.firstOrNull()
            contentToDelete?.let { noteDao.deleteNoteContent(it) }
        }.join()

        // Assert
        var loadedContent:List<NoteWithContent>? = null
        launch {
            loadedContent = noteDao.getAllNotesWithContent()
        }.join()

        assertTrue(loadedContent?.isEmpty() ?: false)
    }

}
