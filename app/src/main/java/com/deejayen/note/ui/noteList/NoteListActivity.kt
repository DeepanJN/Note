package com.deejayen.note.ui.noteList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.database.entity.Note
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

    @Inject
    lateinit var noteListViewModel: NoteListViewModel

    private lateinit var binding: ActivityNoteListBinding

    private val TAG = "NoteListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteListViewModel.getAllNote(this)
        setUpRecyclerView()
        setupClickListeners()
        observerNoteListLivedata()
    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener { intentToNoteDetail() }
    }

    private fun setUpRecyclerView() {
        binding.noteListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.noteListRecyclerView.adapter = noteListRecyclerAdapter
        noteListRecyclerAdapter.callback = object : NoteListRecyclerAdapter.NoteListListener {
            override fun onClickNote(note: Note) {
                intentToNoteDetail(note)
            }
        }
    }

    private fun observerNoteListLivedata() {
        noteListViewModel.notesList.observe(this) { notes ->
            if (notes != null) {
                if (notes.isNotEmpty()) {
                    hideNoDataViews()
                    noteListRecyclerAdapter.setNoteWithDetailList(notes)
                } else {
                    showNoDataViews()
                }
            }
        }
    }

    private fun intentToNoteDetail(note: Note? = null) {
        val intent = Intent(this@NoteListActivity, NoteDetailActivity::class.java)
        note?.let { intent.putExtra(ModelUtil.noteId, it.noteId) }
        startActivity(intent)
    }

    private fun hideNoDataViews() {
        binding.noteListQuoteTv.visibility = View.GONE
        binding.noteListHintTv.visibility = View.GONE
        binding.noteListWelcomeTextView.visibility = View.VISIBLE
        binding.noteListRecyclerView.visibility = View.VISIBLE
    }

    private fun showNoDataViews() {
        binding.noteListQuoteTv.visibility = View.VISIBLE
        binding.noteListHintTv.visibility = View.VISIBLE
        binding.noteListWelcomeTextView.visibility = View.GONE
        binding.noteListRecyclerView.visibility = View.GONE
    }
}
