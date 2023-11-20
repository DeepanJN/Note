package com.deejayen.note

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.deejayen.note.database.NoteDatabase
import com.deejayen.note.database.dao.NoteDao
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteContent
import com.deejayen.note.database.entity.NoteType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class NoteDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var noteDao: NoteDao
    private lateinit var database: NoteDatabase

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java
        ).build()
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
        val noteId = noteDao.insertNote(note)

        // Read
        val loadedNote = noteDao.getNoteWithContentById(noteId.toInt())
        assertNotNull(loadedNote)
        assertEquals("Test Note", loadedNote?.note?.title)
    }

    @Test
    fun insertAndUpdateNoteContent() = runBlocking {

        val oldContentStr = "Content"
        val updatedContentStr = "Updated Content"

        val note = Note(title = "Test Note")
        val noteId = noteDao.insertNote(note)

        val noteContent = NoteContent(noteId = noteId.toInt(), type = NoteType.TEXT, value = oldContentStr)
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

        val noteContent = NoteContent(noteId = noteId.toInt(), type = NoteType.TEXT, value = "Content")
        noteDao.insertNoteContent(noteContent)

        // Delete
        val contentToDelete = noteDao.getAllNotesWithContent()?.firstOrNull()?.noteDetails?.firstOrNull()
        contentToDelete?.let { noteDao.deleteNoteContent(it) }

        // Read
        val loadedContent = noteDao.getAllNotesWithContent()?.firstOrNull()?.noteDetails
        loadedContent?.let { assertTrue(it.isEmpty()) }
    }
}
