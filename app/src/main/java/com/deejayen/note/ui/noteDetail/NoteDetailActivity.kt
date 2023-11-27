package com.deejayen.note.ui.noteDetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.R
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteTextDetail
import com.deejayen.note.databinding.ActivityNoteDetailBinding
import com.deejayen.note.ui.imagePreview.ImagePreviewActivity
import com.deejayen.note.util.ModelUtil
import com.deejayen.note.util.UIUtil.Companion.dpToPx
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class NoteDetailActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var noteDetailViewModel: NoteDetailViewModel

    private lateinit var binding: ActivityNoteDetailBinding

    private var isFirstTimeHeadingUpdate = false
    private var isFirstTimeContentUpdate = false

    val TAG = "NoteDetailActivity"

    private val imagePreviewResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    binding.noteDetailScrollLinerLayout.removeAllViews()
                    noteDetailViewModel.getNoteWithDetailsByNoteId()
                    renderImageToScrollView()
                }

            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied_go_to_settings), Toast.LENGTH_LONG).show()
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    lifecycleScope.launch {
                        handelIntent(intent)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndGetNoteId()

        setupOnClickListeners()

        setupOnTextChangeListeners()

    }

    override fun onBackPressed() {
        if (noteDetailViewModel.checkAnyUpdateJobIsActive()) {
            Toast.makeText(this@NoteDetailActivity.applicationContext, getString(R.string.saving_in_progress_please_wait), Toast.LENGTH_LONG).show()
            return
        } else {
            val selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail.value
            if (selectedNoteWithDetail != null) {
                val textNoteDetail = selectedNoteWithDetail.noteTextDetailList.firstOrNull()?.value
                val note = selectedNoteWithDetail.note
                val noteTitle = note?.title
                val noteId = note?.noteId ?: 0L
                if (noteTitle.isNullOrBlank() && textNoteDetail.isNullOrBlank() && selectedNoteWithDetail.noteImageDetailList.isEmpty() && noteId != 0L) {
                    note?.let { noteDetailViewModel.deleteNote(it) }
                }
            }
            super.onBackPressed()
        }
    }

    private fun checkAndGetNoteId() {
        val noteId = intent.getLongExtra(ModelUtil.noteId, 0L)
        noteDetailViewModel.noteId = noteId
        lifecycleScope.launch(Dispatchers.IO) {
            if (noteId != 0L) {
                noteDetailViewModel.getNoteWithDetailsByNoteId()
                withContext(Dispatchers.Main) {
                    renderUi()
                }
            }
        }
    }

    private suspend fun renderUi() {
        withContext(Dispatchers.Main) {

            val selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail.value
            if (selectedNoteWithDetail != null) {

                isFirstTimeHeadingUpdate = true
                isFirstTimeContentUpdate = true
                val note = selectedNoteWithDetail.note
                val noteTextDetail = selectedNoteWithDetail.noteTextDetailList.firstOrNull()
                binding.noteDetailHeadingTextView.setText(note?.title ?: "")
                binding.noteDetailContentEditText.setText(noteTextDetail?.value ?: "")
                renderImageToScrollView()

            } else {
                val noteWithDetail = NoteWithDetail()
                noteWithDetail.note = Note(title = "")
                noteDetailViewModel.insertOrUpdateNoteWithDetailList(noteWithDetail)
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
                    Log.d(TAG, "afterTextChanged HeadingET")
                    val newText: String = editable.toString()
                    var selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail.value
                    val note = selectedNoteWithDetail?.note
                    if (note != null) {
                        note.title = newText
                    } else {
                        val newNote = Note(title = newText)
                        selectedNoteWithDetail?.note = newNote
                        selectedNoteWithDetail = NoteWithDetail(note = newNote)
                    }

                    noteDetailViewModel.insertOrUpdateNoteWithDetailList(selectedNoteWithDetail)
                    Log.d(TAG, "insertOrUpdateNoteWithDetailList noteDetailHeadingTextView $newText")

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
                    Log.d(TAG, "afterTextChanged DescriptionET")
                    val newText: String = editable.toString()
                    var selectedNoteWithDetail = noteDetailViewModel.selectedNoteWithDetail.value
                    var note = selectedNoteWithDetail?.note
                    var noteTextDetail = selectedNoteWithDetail?.noteTextDetailList?.firstOrNull()
                    if (note != null && noteTextDetail != null) {
                        noteTextDetail.value = newText
                    } else {
                        if (note == null) {
                            note = Note()
                        }
                        if (noteTextDetail == null) {
                            noteTextDetail = NoteTextDetail(value = newText)
                        }
                        selectedNoteWithDetail = NoteWithDetail(note, arrayListOf(noteTextDetail))
                    }

                    Log.d(TAG, "insertOrUpdateNoteWithDetailList noteDetailContentEditText $newText")
                    noteDetailViewModel.insertOrUpdateNoteWithDetailList(selectedNoteWithDetail)
                }
                noteDetailViewModel.setContentTextUpdateJob(job)
            }
        })
    }

    private fun setupOnClickListeners() {

        binding.noteDetailAddImageFab.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        binding.noteDetailToolBarLayout.toolBarBackPressButton.setOnClickListener {
            this.onBackPressed()
        }

    }


    private suspend fun handelIntent(intent: Intent) {
        withContext(Dispatchers.IO) {
            val clipData = intent.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val filePathArrayList = saveImageInBackground(uri)
                    filePathArrayList?.let {
                        noteDetailViewModel.saveImageFileToContent(it)
                        renderImageToScrollView()
                    }

                }
            } else if (intent.data != null) {
                val uri = intent.data!!
                val filePathArrayList = saveImageInBackground(uri)
                filePathArrayList?.let {
                    noteDetailViewModel.saveImageFileToContent(it)
                    renderImageToScrollView()
                }

            }
        }
    }


    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        getContentLauncher.launch(intent)
    }

    private suspend fun saveImageInBackground(uri: android.net.Uri): String? {
        return try {
            withContext(Dispatchers.IO) {
                val file = File(filesDir, "${System.currentTimeMillis()}.jpg")
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                return@withContext file.absolutePath
            }
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun renderImageToScrollView() {
        withContext(Dispatchers.Main) {
            val noteImageDetailList = noteDetailViewModel.selectedNoteWithDetail.value?.noteImageDetailList
            if (noteImageDetailList.isNullOrEmpty()) {
                binding.noteDetailScrollView.visibility = View.GONE
                return@withContext
            }

            binding.noteDetailScrollView.visibility = View.VISIBLE

            noteImageDetailList.forEach { noteImageDetail ->
                val filePath = noteImageDetail.value.orEmpty()
                val file = File(filePath)

                if (file.exists()) {
                    val imageView = createImageView(file)
                    imageView?.let {
                        setImageViewClickAction(it, noteImageDetail.noteImageDetailId)
                        binding.noteDetailScrollLinerLayout.addView(it)
                    }
                }
            }
        }
    }

    private fun createImageView(file: File): ImageView? {
        try {
            val imageView = ImageView(this)
            imageView.layoutParams = LinearLayout.LayoutParams(
                resources.getDimension(R.dimen.image_thumbnail_size).toInt(),
                resources.getDimension(R.dimen.image_thumbnail_size).toInt(),
            )
            picasso.load("file:$file").into(imageView)
            return imageView
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }

    private fun setImageViewClickAction(imageView: ImageView, imageDetailId: Long) {
        imageView.setOnClickListener {
            val intent = Intent(this@NoteDetailActivity, ImagePreviewActivity::class.java).apply {
                putExtra(ModelUtil.noteImageDetailId, imageDetailId)
            }
            imagePreviewResult.launch(intent)
        }
    }


}

