package com.amitbansal.notesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amitbansal.notesapp.models.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao
}