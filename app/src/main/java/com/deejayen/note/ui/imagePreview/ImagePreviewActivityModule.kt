package com.deejayen.note.ui.imagePreview

import androidx.lifecycle.ViewModelProvider
import com.deejayen.note.di.ViewModelProviderFactory
import com.deejayen.note.repository.NoteRepository
import dagger.Module
import dagger.Provides


@Module
class ImagePreviewActivityModule {

    @Provides
    fun provideNoteDetailViewModel(noteRepository: NoteRepository)
            : ImagePreviewViewModel = ImagePreviewViewModel(noteRepository)

    @Provides
    fun provideNoteDetailViewModelFactory(imagePreviewViewModel: ImagePreviewViewModel)
            : ViewModelProvider.Factory = ViewModelProviderFactory(imagePreviewViewModel)

}