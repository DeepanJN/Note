package com.deejayen.note.di

import com.deejayen.note.NoteApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBuilder::class])
interface AppComponent : AndroidInjector<NoteApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<NoteApplication>()
}