package com.deejayen.note.ui.noteList

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.databinding.ActivityNoteListBinding
import com.deejayen.note.ui.noteDetail.NoteDetailActivity
import com.deejayen.note.util.ModelUtil
import dagger.android.support.DaggerAppCompatActivity
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

        binding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteListViewModel = ViewModelProvider(this, viewModelFactory)[NoteListViewModel::class.java]

        noteListViewModel.getAllNoteWithDetail()

        setUpRecyclerView()

        setupClickListeners()

        observerNoteListLivedata()

    }

    override fun onResume() {
        super.onResume()

    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener {
            intentToNoteDetail()
        }
    }

    private fun observerNoteListLivedata() {
        noteListViewModel.notesWithDetailsList.observe(this) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    noteListRecyclerAdapter.setNoteWithDetailList(it as ArrayList<NoteWithDetail>)
                }
            }
        }
    }

    private fun addNewNote() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            noteListViewModel.insertOrUpdateNoteWithDetailList(arrayListOf(MockDataUtil.getMockNoteWithDetail()))
//
//        }
    }

    private fun setUpRecyclerView() {

        binding.noteListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.noteListRecyclerView.adapter = noteListRecyclerAdapter

        noteListRecyclerAdapter.callback = object : (NoteListRecyclerAdapter.NoteListListener) {
            override fun onClickNote(noteWithDetail: NoteWithDetail) {
                intentToNoteDetail(noteWithDetail)
            }
        }
    }

    private fun intentToNoteDetail(noteWithDetail: NoteWithDetail? = null) {
        val intent = Intent(this@NoteListActivity, NoteDetailActivity::class.java)
        noteWithDetail?.let { intent.putExtra(ModelUtil.noteId, noteWithDetail.note.noteId) }
        startActivity(intent)
    }

}

