package com.deejayen.note.ui.noteDetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.R
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteTextDetail
import com.deejayen.note.databinding.ActivityNoteDetailBinding
import com.deejayen.note.util.ModelUtil
import com.deejayen.note.util.UIUtil.Companion.dpToPx
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import javax.inject.Inject

class NoteDetailActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var picasso: Picasso

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


//    override fun onPause() {
//        super.onPause()
//
//    }

    private fun checkAndGetNoteId() {
        lifecycleScope.launch(Dispatchers.IO) {

            val noteId = intent.getLongExtra(ModelUtil.noteId, 0L)

            if (noteId == 0L) {
                val note = Note()
                val noteTextDetail = NoteTextDetail()
                val noteWithDetail = NoteWithDetail(note, listOf(noteTextDetail), listOf())
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
                val noteDetail = noteWithDetail.noteTextDetailList.firstOrNull()
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
                    val noteDetail = selectedNoteWithDetail?.noteTextDetailList?.firstOrNull()
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
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        binding.noteDetailBackPressButton.setOnClickListener {
            this.onBackPressed()
        }

    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission denied. Please go to settings", Toast.LENGTH_LONG).show()
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    val clipData = intent.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            CoroutineScope(Dispatchers.IO).launch {
                                val filePathArrayList = saveImageInBackground(uri)
                                filePathArrayList?.let { renderImageFileToView(it) }
                            }
                        }
                    } else if (intent.data != null) {
                        val uri = intent.data!!
                        CoroutineScope(Dispatchers.IO).launch {
                            val filePathArrayList = saveImageInBackground(uri)
                            filePathArrayList?.let { renderImageFileToView(it) }
                        }
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

    suspend fun renderImageFileToView(vararg filePaths: String) {

        filePaths.forEach { filePath ->

            val imageView = ImageView(this)
            imageView.layoutParams = LinearLayout.LayoutParams(
                resources.dpToPx(100),
                resources.dpToPx(100)
            )

            val file = File(filePath)
            if (file.exists()) {
                withContext(Dispatchers.Main){
                    picasso.load("file:$file").into(imageView)
                    binding.noteDetailScrollLinerLayout.addView(imageView)
                }

            }
        }

    }


}

