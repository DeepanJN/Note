package com.deejayen.note.ui.noteDetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteDetail
import com.deejayen.note.database.entity.NoteType
import com.deejayen.note.databinding.ActivityNoteDetailBinding
import com.deejayen.note.util.ModelUtil
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        checkAndGetNoteId()

        setupOnClickListeners()

        setupOnTextChangeListeners()

    }

    override fun onPause() {
        super.onPause()
        noteDetailViewModel.cancelHeadingTextUpdateJob()
        noteDetailViewModel.cancelContentTextUpdateJob()

    }

    private fun checkAndGetNoteId() {
        lifecycleScope.launch(Dispatchers.IO) {

            val noteId = intent.getLongExtra(ModelUtil.noteId, 0L)

            if (noteId == 0L) {
                val note = Note()
                val noteDetail = NoteDetail()
                noteDetail.type = NoteType.TEXT
                val noteWithDetail = NoteWithDetail(note, arrayListOf(noteDetail))
                noteDetailViewModel.insertOrUpdateNoteWithDetailList(noteWithDetail)
                return@launch
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val noteDetail = noteDetailViewModel.getNoteWithDetailsByNoteId(noteId)
                renderUi()
            }

        }
    }

    private suspend fun renderUi() {
        withContext(Dispatchers.Main) {
            noteDetailViewModel.selectedNoteWithDetail.value?.let { noteWithDetail ->
                val note = noteWithDetail.note
                val noteDetail = noteWithDetail.noteDetailList.firstOrNull { it.type == NoteType.TEXT }
                binding.noteDetailHeadingTextView.setText(note.title ?: "")
                binding.noteDetailContentEditText.setText(noteDetail?.value ?: "")
            }
        }
    }

    private fun setupOnTextChangeListeners() {

        binding.noteDetailHeadingTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun afterTextChanged(editable: Editable?) {
                noteDetailViewModel.cancelHeadingTextUpdateJob()
                val job = lifecycleScope.launch(Dispatchers.Main) {
                    delay(noteDetailViewModel.onTypeDelay)
                    val newText: String = editable.toString()
                    val selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail.value
                    val note = selectedNoteWithDetail?.note
                    note?.title = newText
                    Log.d(TAG, "insertOrUpdateNoteWithDetailList noteDetailHeadingTextView $newText")
                    noteDetailViewModel.insertOrUpdateNoteWithDetailList(selectedNoteWithDetail)
                }
                noteDetailViewModel.setHeadingTextUpdateJob(job)
            }
        })

        binding.noteDetailContentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun afterTextChanged(editable: Editable?) {
                noteDetailViewModel.cancelContentTextUpdateJob()
                val job = lifecycleScope.launch(Dispatchers.Main) {
                    delay(noteDetailViewModel.onTypeDelay)
                    val newText: String = editable.toString()
                    val selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail.value
                    val noteDetail = selectedNoteWithDetail?.noteDetailList?.firstOrNull { it.type == NoteType.TEXT }
                    noteDetail?.value = newText
                    Log.d(TAG, "insertOrUpdateNoteWithDetailList noteDetailContentEditText $newText")
                    noteDetailViewModel.insertOrUpdateNoteWithDetailList(selectedNoteWithDetail)
                }
                noteDetailViewModel.setContentTextUpdateJob(job)
            }
        })
    }

    private fun setupOnClickListeners() {

        binding.noteDetailAddImageFab.setOnClickListener {
            //Add Image to image scroll
        }

    }


}

