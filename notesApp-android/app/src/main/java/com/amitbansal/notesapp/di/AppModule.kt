package com.amitbansal.notesapp.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.amitbansal.notesapp.App
import com.amitbansal.notesapp.db.AppDb
import com.amitbansal.notesapp.util.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppDb(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app,
            AppDb::class.java,
            DB_NAME
        ).build()

    @Singleton
    @Provides
    fun provideNoteDao(db: AppDb) =
        db.getNoteDao()

    @Provides
    fun provideWorkManager(@ApplicationContext app: Context): WorkManager
            = WorkManager.getInstance(app)

}