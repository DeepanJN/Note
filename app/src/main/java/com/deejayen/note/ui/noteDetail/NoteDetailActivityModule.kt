package com.deejayen.note.ui.noteDetail

import androidx.lifecycle.ViewModelProvider
import com.deejayen.note.di.ViewModelProviderFactory
import com.deejayen.note.repository.NoteRepository
import com.deejayen.note.ui.noteList.NoteListRecyclerAdapter
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides

@Module
class NoteDetailActivityModule {

    @Provides
    fun provideNoteDetailViewModel(noteRepository: NoteRepository): NoteDetailViewModel = NoteDetailViewModel(noteRepository)


    @Provides
    fun provideNoteDetailViewModelFactory(noteDetailViewModel: NoteDetailViewModel):
            ViewModelProvider.Factory = ViewModelProviderFactory(noteDetailViewModel)


    @Provides
    fun provideNoteImageDetailRecyclerAdapter(picasso: Picasso): NoteImageDetailRecyclerAdapter = NoteImageDetailRecyclerAdapter(picasso)




}

