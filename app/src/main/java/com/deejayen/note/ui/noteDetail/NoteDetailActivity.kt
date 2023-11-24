package com.deejayen.note.ui.noteDetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.R
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

    private var isFirstTimeHeadingUpdate = true
    private var isFirstTimeContentUpdate = true

    val TAG = "NoteDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDetailViewModel = ViewModelProvider(this, viewModelFactory)[NoteDetailViewModel::class.java]

        checkAndGetNoteId()

        setupOnClickListeners()

        setupOnTextChangeListeners()

//        setUpOnBackPressCallBack()

    }

    override fun onBackPressed() {
        if (noteDetailViewModel.checkAnyUpdateJobIsActive()) {
            Toast.makeText(this@NoteDetailActivity.applicationContext, getString(R.string.saving_in_progress_please_wait), Toast.LENGTH_LONG).show()
        } else {
            super.onBackPressed()
        }

    }

//    private fun NoteDetailActivity.setUpOnBackPressCallBack() {
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (noteDetailViewModel.checkBothCoroutineJobsAreCompleted()) {
//                    Toast.makeText(this@NoteDetailActivity.applicationContext, getString(R.string.saving_in_progress_please_wait), Toast.LENGTH_LONG).show()
//                } else {
//                    onBackPressed()
//                }
//            }
//        }
//
//        onBackPressedDispatcher.addCallback(this, callback)
//    }

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
                noteDetailViewModel.getNoteWithDetailsByNoteId(noteId)
                renderUi()
            }

        }
    }

    private suspend fun renderUi() {
        isFirstTimeHeadingUpdate = true
        isFirstTimeContentUpdate = true
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
                if (isFirstTimeHeadingUpdate) {
                    isFirstTimeHeadingUpdate = false
                    return
                }
                noteDetailViewModel.cancelHeadingTextUpdateJob()
                val job = lifecycleScope.launch(Dispatchers.Main) {
                    delay(noteDetailViewModel.ON_TYPE_DELAY)
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
                if (isFirstTimeContentUpdate) {
                    isFirstTimeContentUpdate = false
                    return
                }
                noteDetailViewModel.cancelContentTextUpdateJob()
                val job = lifecycleScope.launch(Dispatchers.Main) {
                    delay(noteDetailViewModel.ON_TYPE_DELAY)
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

//        binding.noteDetailMainLayout.setOnClickListener {
//            binding.noteDetailContentEditText.requestFocus()
//        }

    }


}

