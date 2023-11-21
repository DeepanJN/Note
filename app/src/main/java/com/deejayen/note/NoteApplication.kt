package com.deejayen.note

import com.deejayen.note.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class NoteApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<NoteApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}