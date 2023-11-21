package com.deejayen.note.ui.noteList

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.R
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail
import com.deejayen.note.database.entity.NoteType
import com.deejayen.note.util.MockDataUtil
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteListActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var noteListViewModel: NoteListViewModel

    val TAG = "NoteListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_note_list)

        noteListViewModel = ViewModelProvider(this, viewModelFactory)[NoteListViewModel::class.java]

        noteListViewModel.getAllNoteWithDetail()

        //Recycler adapter

        lifecycleScope.launch(Dispatchers.IO) {
            noteListViewModel.insertOrUpdateNoteWithDetailList(arrayListOf(MockDataUtil.getMockNoteWithDetail()))

        }

        noteListViewModel.notesWithDetailsList.observe(this) {
            if (it != null) {
                Log.d(TAG, "${it.count()}")
            }
        }


    }
}

