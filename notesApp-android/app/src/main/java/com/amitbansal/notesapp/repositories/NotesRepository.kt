package com.amitbansal.notesapp.repositories

import com.amitbansal.notesapp.api.RetrofitInstance
import com.amitbansal.notesapp.db.NoteDao
import com.amitbansal.notesapp.models.Note
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllDbNotes() = noteDao.getAllNotes()

    suspend fun getNotesFromApi(page: Int) = RetrofitInstance.notesApi.getAll(page)

    suspend fun addAll(notes: List<Note>) = noteDao.insertAll(notes)

    suspend fun add(note: Note) = noteDao.insert(note)

    suspend fun makePublic(note: Note) = RetrofitInstance.notesApi.makePublic(note.id)

    suspend fun makePrivate(note: Note) = RetrofitInstance.notesApi.makePrivate(note.id)

    suspend fun deleteAll() = noteDao.deleteAll()
}