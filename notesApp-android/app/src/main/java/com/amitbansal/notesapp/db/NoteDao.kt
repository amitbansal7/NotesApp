package com.amitbansal.notesapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amitbansal.notesapp.models.Note

@Dao
interface NoteDao {

    @Query("select * from notes order by updated_at desc")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Query("delete from notes where sync = :sync")
    suspend fun deleteAllSync(sync: Boolean = true)

    @Query("delete from notes")
    suspend fun deleteAll()

    @Query("select * from notes where sync = :sync")
    suspend fun getAllSync(sync: Boolean): List<Note>

    @Query("delete from notes where id = :id")
    suspend fun deleteById(id: Int)
}