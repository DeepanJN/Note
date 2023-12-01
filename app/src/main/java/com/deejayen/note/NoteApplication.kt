package com.deejayen.note

import com.deejayen.note.di.DaggerAppComponent
import com.google.android.material.color.DynamicColors
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class NoteApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun applicationInjector(): AndroidInjector<NoteApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}