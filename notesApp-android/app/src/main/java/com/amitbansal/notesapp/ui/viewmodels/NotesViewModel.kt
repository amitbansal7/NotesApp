package com.amitbansal.notesapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amitbansal.notesapp.models.Note
import com.amitbansal.notesapp.models.NoteResponse
import com.amitbansal.notesapp.models.NotesResponse
import com.amitbansal.notesapp.repositories.NotesRepository
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class NotesViewModel @ViewModelInject constructor(
    private val notesRepository: NotesRepository
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
    }

    fun refreshNotes() = viewModelScope.launch(Dispatchers.IO) {
        if (Utils.hasInternetConnection()) {
            swipeRefreshStatus.postValue(Resource.Success(false))
            val notesFromApi = handleNotesResponse(notesRepository.getNotesFromApi(1))
            notesPage = 1
            notesRepository.deleteAll()
            notesResponse.postValue(notesFromApi)
            notesFromApi.data?.notes?.let { notesRepository.addAll(it) }
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
            notesFromApi.data?.notes?.let { notesRepository.addAll(it) }
            notesResponse.postValue(notesFromApi)
        } else {
            notesResponse.postValue(Resource.Error(data = null, message = "No Internet Connection"))
        }
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        updateNoteHelper(
            { notesRepository.updateNote(note) },
            updateNoteResponse,
            {
                val savedNote = note.copy(sync = false)
                notesRepository.add(savedNote)
                makePublicNoteResponse.postValue(
                    Resource.Success(NoteResponse(savedNote, "Update Note locally"))
                )
            }
        )

    }

    fun createNote(title: String, text: String) = viewModelScope.launch(Dispatchers.IO) {
        updateNoteHelper(
            { notesRepository.createNote(title, text) },
            createNoteResponse,
            {
//                val savedNote = note.copy(sync = false)
//                notesRepository.add(savedNote)
//                makePublicNoteResponse.postValue(
//                    Resource.Success(NoteResponse(savedNote, "Note Created locally"))
//                )
            }
        )
    }

    fun makeNotePublic(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        updateNoteHelper(
            { notesRepository.makePublic(note) },
            makePublicNoteResponse,
            {
                updateNoteResponse.postValue(Resource.Error(null, "No Internet Connection"))
            }
        )
    }

    fun makeNotePrivate(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        updateNoteHelper(
            { notesRepository.makePrivate(note) },
            updateNoteResponse,
            {
                updateNoteResponse.postValue(Resource.Error(null, "No Internet Connection"))
            }
        )
    }

    private suspend fun updateNoteHelper(
        call: suspend () -> Response<NoteResponse>,
        responseObj: MutableLiveData<Resource<NoteResponse>>,
        noInternetConnectionCall: suspend () -> Unit
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            if (Utils.hasInternetConnection()) {
                responseObj.postValue(Resource.Loading())
                val response = handleNoteResponse(
                    call()
                )
                response.data?.note?.let {
                    notesRepository.add(it)
                }
                responseObj.postValue(response)

            } else {
                noInternetConnectionCall()
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