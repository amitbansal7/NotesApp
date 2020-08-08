package com.amitbansal.notesapp.repositories

import com.amitbansal.notesapp.api.RetrofitInstance
import com.amitbansal.notesapp.db.NoteDao
import com.amitbansal.notesapp.models.Note
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllDbNotes() = noteDao.getAllNotes()

    suspend fun getNotesFromApi() = RetrofitInstance.notesApi.getAll()

    suspend fun addAll(notes: List<Note>) = noteDao.insertAll(notes)

    suspend fun add(note: Note) = noteDao.insert(note)

    suspend fun deleteAll() = noteDao.deleteAll()
}