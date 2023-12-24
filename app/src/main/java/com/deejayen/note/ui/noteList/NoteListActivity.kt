package com.deejayen.note.ui.noteList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deejayen.note.R
import com.deejayen.note.database.entity.Note
import com.deejayen.note.databinding.ActivityNoteListBinding
import com.deejayen.note.ui.noteDetail.NoteDetailActivity
import com.deejayen.note.util.ModelUtil
import com.deejayen.note.util.UIUtil.Companion.showMaterialAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteListActivity : AppCompatActivity() {

    @Inject
    lateinit var noteListRecyclerAdapter: NoteListRecyclerAdapter


    private val noteListViewModel: NoteListViewModel by viewModels()

    private lateinit var binding: ActivityNoteListBinding

    private val TAG = "NoteListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteListViewModel.getAllNote()
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

            override fun onLongClick(note: Note) {
                this@NoteListActivity.showMaterialAlertDialog(
                    R.string.delete_note, R.string.delete_note_description
                ) { isPositive ->
                    if (isPositive) {
                        note.let {
                            lifecycleScope.launch(Dispatchers.IO) {
                                noteListViewModel.deleteNote(it)
                            }
                        }
                    }
                }
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
