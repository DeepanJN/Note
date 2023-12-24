package com.deejayen.note.ui.imagePreview

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.R
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.databinding.ActivityImagePreviewBinding
import com.deejayen.note.util.ModelUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ImagePreviewActivity : AppCompatActivity() {

    @Inject
    lateinit var picasso: Picasso

    private val imagePreviewViewModel: ImagePreviewViewModel by viewModels()

    private lateinit var binding: ActivityImagePreviewBinding

    val TAG = "ImagePreviewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePreviewViewModel.noteImageDetailId = intent.getLongExtra(ModelUtil.noteImageDetailId, 0L)

        lifecycleScope.launch(Dispatchers.IO) {
            val noteImageDetail = imagePreviewViewModel.getImageDetailForImageDetailId()
            renderImage()
            withContext(Dispatchers.Main) {
                setupOnClickListeners()
            }
        }
    }


    private fun setupOnClickListeners() {
        val noteImageDetail = imagePreviewViewModel.noteImageDetail ?: return
        binding.previewImageDeleteButton.setOnClickListener {
            showDeleteConfirmationDialog(noteImageDetail)
        }

        binding.previewToolBarBackPressButton.setOnClickListener {
            finish()
        }
    }

    private fun showDeleteConfirmationDialog(noteImageDetail: NoteImageDetail) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.delete_image))
            setMessage(getString(R.string.are_you_sure_you_want_to_delete_image))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                handleDeleteImageDetail(noteImageDetail)
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }

    }

    private suspend fun renderImage() {
        val noteImageDetail = imagePreviewViewModel.noteImageDetail
        val imageFilePath = noteImageDetail?.value
        val file = imageFilePath?.let { File(it) }

        try {
            if (file?.exists() == true) {
                withContext(Dispatchers.Main) {
                    loadPreviewImage(file)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    private suspend fun loadPreviewImage(file: File) {
        withContext(Dispatchers.Main) {
            val imageView = binding.previewImageView
            picasso.load("file:$file")
                .into(imageView)
        }
    }

    private fun handleDeleteImageDetail(noteImageDetail: NoteImageDetail) {
        imagePreviewViewModel.deleteNoteImageDetail(noteImageDetail)
        setResult(RESULT_OK)
        finish()
    }

}
