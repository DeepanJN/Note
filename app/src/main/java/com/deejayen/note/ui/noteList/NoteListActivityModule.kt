package com.deejayen.note.ui.noteList

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.deejayen.note.di.ViewModelProviderFactory
import com.deejayen.note.repository.NoteRepository
import dagger.Module
import dagger.Provides

@Module
class NoteListActivityModule {

    @Provides
    fun provideNoteListViewModel(noteRepository: NoteRepository): NoteListViewModel = NoteListViewModel(noteRepository)


    @Provides
    fun provideNoteListViewModelFactory(noteListViewModel: NoteListViewModel):
            ViewModelProvider.Factory = ViewModelProviderFactory(noteListViewModel)

    @Provides
    fun provideNoteListRecyclerAdapter():
            NoteListRecyclerAdapter = NoteListRecyclerAdapter()

}

