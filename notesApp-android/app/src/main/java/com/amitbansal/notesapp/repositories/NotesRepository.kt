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

    suspend fun makePublic(sid: Int) = RetrofitInstance.notesApi.makePublic(sid)

    suspend fun makePrivate(sid: Int) = RetrofitInstance.notesApi.makePrivate(sid)

    suspend fun createNote(title: String, text: String) =
        RetrofitInstance.notesApi.createNote(title, text)

    suspend fun updateNote(sid: Int, title: String, text: String?) =
        RetrofitInstance.notesApi.updateNote(sid, title, text)

    suspend fun deleteAll() = noteDao.deleteAll()

    suspend fun getAllNotSynced() = noteDao.getAllSync(false)

    suspend fun deleteById(id: Int) = noteDao.deleteById(id)
}