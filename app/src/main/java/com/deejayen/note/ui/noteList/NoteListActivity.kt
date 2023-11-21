package com.deejayen.note.ui.noteList

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.deejayen.note.R
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class NoteListActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var noteListViewModel: NoteListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_note_list)

        noteListViewModel = ViewModelProvider(this, viewModelFactory)[NoteListViewModel::class.java]

    }
}

