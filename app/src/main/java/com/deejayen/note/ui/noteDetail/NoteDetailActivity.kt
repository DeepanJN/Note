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
import com.deejayen.note.databinding.ActivityNoteListBinding
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

        //Edit text

        //List of images

        //Edit text for detail

    }

    private fun checkAndGetNoteId() {

        val noteId = intent.getLongExtra(ModelUtil.noteId, 0L)
        if (noteId == 0L) {
            val note = Note()
            val noteDetail = NoteDetail()
            noteDetail.type = NoteType.TEXT
            val noteWithDetail = NoteWithDetail(note, arrayListOf(noteDetail))
            lifecycleScope.launch(Dispatchers.IO) {
                noteDetailViewModel.insertOrUpdateNoteWithDetailList(noteWithDetail)
            }
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val noteDetail = noteDetailViewModel.getNoteWithDetailsByNoteId(noteId)
            runOnUiThread {
                renderUi()
            }
        }
    }

    private fun renderUi() {
        noteDetailViewModel.selectedNoteWithDetail?.let { noteWithDetail ->
            val note = noteWithDetail.note
            val noteDetail = noteWithDetail.noteDetailList.filter { it.type == NoteType.TEXT }.firstOrNull()
            binding.noteDetailHeadingTextView.setText(note.title ?: "")
            binding.noteDetailContentEditText.setText(noteDetail?.value ?: "")
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
                noteDetailViewModel.headingTextUpdateJob?.cancel()
                noteDetailViewModel.headingTextUpdateJob = lifecycleScope.launch {
                    delay(noteDetailViewModel.onTypeDelay)
                    val newText: String = editable.toString()
                    val selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail
                    val note = selectedNoteWithDetail?.note //need to optimise
                    note?.title = newText
                    Log.d(TAG, "insertOrUpdateNoteWithDetailList noteDetailHeadingTextView $newText")
                    noteDetailViewModel.insertOrUpdateNoteWithDetailList(selectedNoteWithDetail)
                }
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
                noteDetailViewModel.contentTextUpdateJob?.cancel()
                noteDetailViewModel.contentTextUpdateJob = lifecycleScope.launch {
                    delay(noteDetailViewModel.onTypeDelay)
                    val newText: String = editable.toString()
                    val noteDetail = noteDetailViewModel.selectedNoteWithDetail?.noteDetailList?.filter { it.type == NoteType.TEXT }?.firstOrNull() //need to optimise
                    noteDetail?.value = newText
                    Log.d(TAG, "insertOrUpdateNoteWithDetailList noteDetailContentEditText $newText")
                    noteDetailViewModel.insertOrUpdateNoteWithDetailList(noteDetailViewModel.selectedNoteWithDetail)
                }
            }
        })


    }

    private fun setupOnClickListeners() {


        binding.noteDetailAddImageFab.setOnClickListener {
            //Add Image to image scroll
        }

    }


}

