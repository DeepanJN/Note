package com.deejayen.note.ui.noteDetail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.deejayen.note.databinding.ActivityNoteDetailBinding
import com.deejayen.note.databinding.ActivityNoteListBinding
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class NoteDetailActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var noteDetailViewModel: NoteDetailViewModel

    private lateinit var binding: ActivityNoteDetailBinding

    val TAG = "NoteDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDetailViewModel = ViewModelProvider(this, viewModelFactory)[NoteDetailViewModel::class.java]


    }


}

