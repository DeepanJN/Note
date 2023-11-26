package com.deejayen.note.di

import com.deejayen.note.ui.imagePreview.ImagePreviewActivity
import com.deejayen.note.ui.imagePreview.ImagePreviewActivityModule
import com.deejayen.note.ui.noteDetail.NoteDetailActivity
import com.deejayen.note.ui.noteDetail.NoteDetailActivityModule
import com.deejayen.note.ui.noteList.NoteListActivity
import com.deejayen.note.ui.noteList.NoteListActivityModule
import com.deejayen.note.ui.noteList.NoteListViewModel
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [NoteListActivityModule::class])
    abstract fun bindNoteListActivity(): NoteListActivity

    @ContributesAndroidInjector(modules = [NoteDetailActivityModule::class])
    abstract fun bindNoteDetailActivity(): NoteDetailActivity

    @ContributesAndroidInjector(modules = [ImagePreviewActivityModule::class])
    abstract fun bindImagePreviewActivity(): ImagePreviewActivity

}