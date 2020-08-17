package com.amitbansal.notesapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.amitbansal.notesapp.models.Note
import com.amitbansal.notesapp.models.NoteResponse
import com.amitbansal.notesapp.models.NotesResponse
import com.amitbansal.notesapp.repositories.NotesRepository
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils
import com.amitbansal.notesapp.workers.NoteSyncWorker
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class NotesViewModel @ViewModelInject constructor(
    private val notesRepository: NotesRepository,
    private val workManager: WorkManager
) : ViewModel() {

    val notesResponse: MutableLiveData<Resource<NotesResponse>> = MutableLiveData()
    val swipeRefreshStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val updateNoteResponse: MutableLiveData<Resource<NoteResponse>> = MutableLiveData()
    val makePublicNoteResponse: MutableLiveData<Resource<NoteResponse>> = MutableLiveData()
    val createNoteResponse: MutableLiveData<Resource<NoteResponse>> = MutableLiveData()


    var notes: LiveData<List<Note>> = notesRepository.getAllDbNotes()
    var notesPage = 1

    init {
        getNotes(true)
        workManager.enqueue(OneTimeWorkRequest.from(NoteSyncWorker::class.java))
    }

    fun refreshNotes() = viewModelScope.launch(Dispatchers.IO) {
        if (Utils.hasInternetConnection()) {
            swipeRefreshStatus.postValue(Resource.Success(false))
            val notesFromApi = handleNotesResponse(notesRepository.getNotesFromApi(1))
            notesPage = 1
            notesRepository.deleteAll()
            notesResponse.postValue(notesFromApi)
            notesFromApi.data?.notes?.let { notesRepository.addAll(filterUnsyncedNotes(it)) }
            swipeRefreshStatus.postValue(Resource.Success(true))
        } else {
            swipeRefreshStatus.postValue(Resource.Error(true, "No Internet Connection"))
        }
    }

    fun getNotes(firstCall: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (Utils.hasInternetConnection()) {
            notesResponse.postValue(Resource.Loading())
            val notesFromApi = handleNotesResponse(notesRepository.getNotesFromApi(notesPage++))
            if (firstCall) {
                notesPage = 1
                notesRepository.deleteAll()
            }
            notesFromApi.data?.notes?.let {
                notesRepository.addAll(filterUnsyncedNotes(it))
            }
            notesResponse.postValue(notesFromApi)
        } else {
            notesResponse.postValue(Resource.Error(data = null, message = "No Internet Connection"))
        }
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        if (Utils.hasInternetConnection()) {
            updateNoteResponse.postValue(Resource.Loading())
            note.note_id?.let {
                val response =
                    handleNoteResponse(notesRepository.updateNote(it, note.title, note.text))

                response.data?.note?.let { note ->
                    notesRepository.add(note)
                }
                updateNoteResponse.postValue(response)
            } ?: updateNoteLocally(note)

        } else {
            updateNoteLocally(note)
        }
    }

    private suspend fun filterUnsyncedNotes(notes: List<Note>): List<Note> {
        val unSyncedIds = notesRepository.getAllNotSynced().map { note -> note.note_id }
        return notes.filter { note -> !unSyncedIds.contains(note.note_id) }
    }

    private suspend fun updateNoteLocally(note: Note) {
        val savedNote = note.copy(sync = false)
        notesRepository.add(savedNote)
        updateNoteResponse.postValue(
            Resource.Success(NoteResponse(savedNote, "Note Updated locally"))
        )
    }

    fun createNote(title: String, text: String) = viewModelScope.launch(Dispatchers.IO) {
        if (Utils.hasInternetConnection()) {
            createNoteResponse.postValue(Resource.Loading())
            val response = handleNoteResponse(
                notesRepository.createNote(title, text)
            )

            response.data?.note?.let {
                notesRepository.add(it)
            }
            createNoteResponse.postValue(response)

        } else {
            val savedNote = Note(title, text, false)
            notesRepository.add(savedNote)
            createNoteResponse.postValue(
                Resource.Success(NoteResponse(savedNote, "Note Created locally"))
            )
        }
    }


    fun makeNotePublic(note: Note) = viewModelScope.launch {
        publicPrivateNoteHelper(makePublicNoteResponse, note, true)
    }

    fun makeNotePrivate(note: Note) = viewModelScope.launch {
        publicPrivateNoteHelper(updateNoteResponse, note, false)
    }

    private suspend fun publicPrivateNoteHelper(
        responseObj: MutableLiveData<Resource<NoteResponse>>,
        note: Note,
        makePublic: Boolean
    ) {
        if (Utils.hasInternetConnection()) {
            responseObj.postValue(Resource.Loading())

            note.note_id?.let {
                val response = handleNoteResponse(
                    if (makePublic) notesRepository.makePublic(it)
                    else notesRepository.makePrivate(it)
                )

                response.data?.note?.let { note ->
                    notesRepository.add(note)
                }
                responseObj.postValue(response)
            } ?: responseObj.postValue(Resource.Error(null, "Note is not synced with server yet."))


        } else {
            responseObj.postValue(Resource.Error(null, "No Internet Connection"))
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

    private fun handleNotesResponse(response: Response<NotesResponse>): Resource<NotesResponse> {
        return if (response.isSuccessful) {
            val notesResponse = response.body()!!
            //All notes from api are by default synced.
            notesResponse.notes = notesResponse.notes.map { it.copy(sync = true) }.toMutableList()
            Resource.Success(notesResponse)
        } else {
            val notesResponse =
                Gson().fromJson(response.errorBody()!!.charStream(), NotesResponse::class.java)

            Resource.Error(null, notesResponse.message)
        }
    }
}