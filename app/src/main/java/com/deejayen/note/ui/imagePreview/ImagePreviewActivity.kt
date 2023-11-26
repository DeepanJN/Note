package com.deejayen.note.ui.imagePreview

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.deejayen.note.database.entity.NoteImageDetail
import com.deejayen.note.databinding.ActivityImagePreviewBinding
import com.deejayen.note.util.ModelUtil
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ImagePreviewActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var imagePreviewViewModel: ImagePreviewViewModel

    private lateinit var binding: ActivityImagePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePreviewViewModel.noteImageDetailId = intent.getLongExtra(ModelUtil.noteImageDetailId, 0L)

        lifecycleScope.launch(Dispatchers.IO) {
            val noteImageDetail = imagePreviewViewModel.getImageDetailForImageDetailId()
            renderImage(noteImageDetail)
            setupOnClickListeners(noteImageDetail)
        }


    }

    private suspend fun setupOnClickListeners(noteImageDetail: NoteImageDetail?) {
        noteImageDetail ?: return
        withContext(Dispatchers.Main) {
            binding.previewImageDeleteButton.setOnClickListener {
                imagePreviewViewModel.deleteNoteImageDetail(noteImageDetail)
                setResult(RESULT_OK)
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private suspend fun renderImage(noteImageDetail: NoteImageDetail?) {
        val imageFilePath = noteImageDetail?.value
        val file = imageFilePath?.let { File(it) }
        if (file?.exists() == true) {
            withContext(Dispatchers.Main) {
                val imageView = binding.previewImageView
                picasso.load("file:$file").into(imageView)
            }
        } else {
            //Error occurred file sending file
        }
    }


}