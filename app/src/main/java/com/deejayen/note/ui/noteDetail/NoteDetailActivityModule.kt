package com.deejayen.note.ui.noteDetail

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.deejayen.note.di.ViewModelProviderFactory
import com.deejayen.note.repository.NoteRepository
import com.deejayen.note.ui.noteList.NoteListViewModel
import dagger.Module
import dagger.Provides

@Module
class NoteDetailActivityModule {

    @Provides
    fun provideNoteDetailViewModel(noteRepository: NoteRepository): NoteDetailViewModel = NoteDetailViewModel(noteRepository)


    @Provides
    fun provideNoteDetailViewModelFactory(noteDetailViewModel: NoteDetailViewModel):
            ViewModelProvider.Factory = ViewModelProviderFactory(noteDetailViewModel)


}

