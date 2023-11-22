package com.deejayen.note.ui.noteList

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.R
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail
import com.deejayen.note.database.entity.NoteType
import com.deejayen.note.databinding.ActivityNoteListBinding
import com.deejayen.note.util.MockDataUtil
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteListActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var noteListRecyclerAdapter: NoteListRecyclerAdapter

    private lateinit var noteListViewModel: NoteListViewModel

    private lateinit var binding: ActivityNoteListBinding

    val TAG = "NoteListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_note_list)

        binding = ActivityNoteListBinding.inflate(layoutInflater)

        noteListViewModel = ViewModelProvider(this, viewModelFactory)[NoteListViewModel::class.java]

        noteListViewModel.getAllNoteWithDetail()

        setUpRecyclerView()

        setupClickListeners()

        observerNoteListLivedata()

    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener {

        }
    }

    private fun observerNoteListLivedata() {
        noteListViewModel.notesWithDetailsList.observe(this) {
            if (it != null) {
                if (it.isNotEmpty()){
                    noteListRecyclerAdapter.setNoteWithDetailList(it as ArrayList<NoteWithDetail>)
                }
            }
        }
    }

    private fun addData() {
        lifecycleScope.launch(Dispatchers.IO) {
            noteListViewModel.insertOrUpdateNoteWithDetailList(arrayListOf(MockDataUtil.getMockNoteWithDetail()))

        }
    }

    private fun setUpRecyclerView() {

        binding.noteListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.noteListRecyclerView.adapter = noteListRecyclerAdapter

        noteListRecyclerAdapter.callback = object : (NoteListRecyclerAdapter.NoteListListener) {
            override fun onClickNote(noteWithDetail: NoteWithDetail) {
                //Intent to detail activity
            }
        }
    }

}

