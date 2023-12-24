package com.deejayen.note.ui.noteDetail

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.R
import com.deejayen.note.database.NoteWithDetail
import com.deejayen.note.database.entity.Note
import com.deejayen.note.database.entity.NoteTextDetail
import com.deejayen.note.databinding.ActivityNoteDetailBinding
import com.deejayen.note.ui.imagePreview.ImagePreviewActivity
import com.deejayen.note.util.ModelUtil
import com.deejayen.note.util.PermissionUtil
import com.deejayen.note.util.UIUtil.Companion.setupAfterTextChangedListener
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private var headingTextUpdateJob: Job? = null
    private var contentTextUpdateJob: Job? = null

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
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val hasPermission: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.READ_MEDIA_IMAGES]
            } else {
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE]
            } ?: false

            if (hasPermission) {
                openGallery()
            } else {

                val shouldShowRequestPermissionRationale: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                if (!shouldShowRequestPermissionRationale) {
                    //Permission from settings
                } else {
                    // Denied
                }
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    lifecycleScope.launch {
                        handelGalleryIntent(intent)
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
        if (checkAnyUpdateJobIsActive()) {
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

        binding.noteDetailHeadingTextView.setupAfterTextChangedListener { editable ->
            if (isFirstTimeHeadingUpdate) {
                isFirstTimeHeadingUpdate = false
                return@setupAfterTextChangedListener
            }
            headingTextUpdateJob?.cancel()
            val job = lifecycleScope.launch(Dispatchers.Main) {
                delay(ModelUtil.ON_TYPE_DELAY)
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
            headingTextUpdateJob = job
        }

        binding.noteDetailContentEditText.setupAfterTextChangedListener { editable ->
            if (isFirstTimeContentUpdate) {
                isFirstTimeContentUpdate = false
                return@setupAfterTextChangedListener
            }
            contentTextUpdateJob?.cancel()
            val job = lifecycleScope.launch(Dispatchers.Main) {
                delay(ModelUtil.ON_TYPE_DELAY)
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
            contentTextUpdateJob = job
        }

    }

    private fun setupOnClickListeners() {

        binding.noteDetailAddImageFab.setOnClickListener {
            if (PermissionUtil.hasReadImagesPermissions(this)) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        binding.noteDetailToolBarLayout.toolBarBackPressButton.setOnClickListener {
            this.onBackPressed()
        }

    }


    private suspend fun handelGalleryIntent(intent: Intent) {
        withContext(Dispatchers.IO) {
            val clipData = intent.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val filePathArrayList = saveImageInBackground(uri)
                    filePathArrayList?.let {
                        noteDetailViewModel.saveImageFileToContent(it)
                        renderImageToScrollView(true)
                    }

                }
            } else if (intent.data != null) {
                val uri = intent.data!!
                val filePathArrayList = saveImageInBackground(uri)
                filePathArrayList?.let {
                    noteDetailViewModel.saveImageFileToContent(it)
                    renderImageToScrollView(true)
                }

            }
        }
    }


    private fun requestPermission() {
        //TODO: check rationale and launch
        requestPermissionLauncher.launch(PermissionUtil.getReadImagesPermissions())
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

    suspend fun renderImageToScrollView(removeAllView: Boolean = false) {
        withContext(Dispatchers.Main) {
            if (removeAllView) {
                binding.noteDetailScrollLinerLayout.removeAllViews()
            }
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

    private suspend fun createImageView(file: File): ImageView? {
        return withContext(Dispatchers.Main) {
            try {
                val imageView = ImageView(this@NoteDetailActivity)
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.absolutePath, options)
                val imageWidth = options.outWidth
                val imageHeight = options.outHeight

                val aspectRatio = imageWidth.toFloat() / imageHeight.toFloat()
                val thumbnailHeight = resources.getDimensionPixelSize(R.dimen.image_thumbnail_size)
                val dynamicWidth = (thumbnailHeight * aspectRatio).toInt()

                val layoutParams = LinearLayout.LayoutParams(dynamicWidth, thumbnailHeight)
                layoutParams.setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.image_padding), 0)
                imageView.layoutParams = layoutParams

                picasso.load("file:$file")
                    .resize(dynamicWidth, thumbnailHeight)
                    .onlyScaleDown()
                    .into(imageView)

                return@withContext imageView
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            return@withContext null
        }
    }

    private fun setImageViewClickAction(imageView: ImageView, imageDetailId: Long) {
        imageView.setOnClickListener {
            val intent = Intent(this@NoteDetailActivity, ImagePreviewActivity::class.java).apply {
                putExtra(ModelUtil.noteImageDetailId, imageDetailId)
            }
            imagePreviewResult.launch(intent)
        }
    }

    fun checkAnyUpdateJobIsActive(): Boolean {
        return headingTextUpdateJob?.isActive ?: false || contentTextUpdateJob?.isActive ?: false
    }

}

