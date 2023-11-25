package com.deejayen.note.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.deejayen.note.NoteApplication
import com.deejayen.note.database.NoteDatabase
import com.deejayen.note.database.NoteDatabase.Companion.DATABASE_NAME
import com.deejayen.note.database.dao.NoteDao
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(noteApplication: NoteApplication): Context = noteApplication.applicationContext

    @Provides
    @Singleton
    fun provideNoteDatabase(context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(appDatabase: NoteDatabase): NoteDao {
        return appDatabase.noteDao
    }

    @Provides
    @Singleton
    fun providePicasso(context: Context): Picasso {
        return Picasso.Builder(context).build()
    }


}