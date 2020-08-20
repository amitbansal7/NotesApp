package com.amitbansal.notesapp.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amitbansal.notesapp.models.Note
import com.amitbansal.notesapp.models.NoteResponse
import com.amitbansal.notesapp.models.NotesResponse
import com.amitbansal.notesapp.repositories.NotesRepository
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils
import com.google.gson.Gson
import retrofit2.Response
import timber.log.Timber

class NoteSyncWorker @WorkerInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val notesRepository: NotesRepository
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {

        Timber.d("Worker starting......")

        if (Utils.hasInternetConnection()) {
            syncNotes()
        } else {
            Timber.e("Worker Started. No Internet connection")
        }

        return Result.success()
    }

    private suspend fun syncNotes() {
        for (note in notesRepository.getAllNotSynced()) {
            note.note_id?.let {
                updateNote(note)
            } ?: run {
                createNote(note)
            }
        }
    }

    private suspend fun createNote(note: Note) {
        val response =
            handleNoteResponse(notesRepository.createNote(note.title, note.text ?: ""))
        when (response) {
            is Resource.Success -> {
                note.id?.let { notesRepository.deleteById(it) }
                response.data?.note?.let { notesRepository.add(it) }
            }
            is Resource.Error -> {
                Timber.e(
                    "Worker: Failed to Create note ${response.message ?: response.data?.message}"
                )
            }
        }
    }

    private suspend fun updateNote(note: Note) {
        val response =
            handleNoteResponse(notesRepository.updateNote(note.note_id!!, note.title, note.text))
        Timber.d("Updating Note ${note}")
        when (response) {
            is Resource.Success -> {
                note.id?.let { notesRepository.deleteById(it) }
                response.data?.note?.let { notesRepository.add(it) }
            }
            is Resource.Error -> {
                Timber.e(
                    "Worker: Failed to update note ${response.message ?: response.data?.message}"
                )
            }
        }
    }

    private fun handleNoteResponse(response: Response<NoteResponse>): Resource<NoteResponse> {
        return if (response.isSuccessful) {
            val noteResponse = response.body()!!
            //All notes from api are by default synced.
            noteResponse.note = noteResponse.note.copy(sync = true)
            Resource.Success(noteResponse)
        } else {
            val notesResponse =
                Gson().fromJson(response.errorBody()!!.charStream(), NotesResponse::class.java)

            Resource.Error(null, notesResponse.message)
        }
    }


}