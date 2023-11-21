package com.deejayen.note.di

import com.deejayen.note.ui.noteList.NoteListActivity
import com.deejayen.note.ui.noteList.NoteListActivityModule
import com.deejayen.note.ui.noteList.NoteListViewModel
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [NoteListActivityModule::class])
    abstract fun bindNoteListActivity(): NoteListActivity

}