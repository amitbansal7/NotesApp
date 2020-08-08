package com.amitbansal.notesapp.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.amitbansal.notesapp.models.Note

@Dao
interface NoteDao {

    @Query("select * from notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Query("delete from notes where sync = :sync")
    suspend fun deleteAll(sync: Boolean = true)
}