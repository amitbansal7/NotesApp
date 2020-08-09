package com.amitbansal.notesapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amitbansal.notesapp.models.Note
import com.amitbansal.notesapp.models.NotesResponse
import com.amitbansal.notesapp.repositories.NotesRepository
import com.amitbansal.notesapp.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class NotesViewModel @ViewModelInject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    val notesResponse: MutableLiveData<Resource<NotesResponse>> = MutableLiveData()
    val swipeRefreshStatus: MutableLiveData<Boolean> = MutableLiveData()

    var notes: LiveData<List<Note>> = notesRepository.getAllDbNotes()
    var notesPage = 1

    init {
        getNotes(true)
    }

    fun refreshNotes() = viewModelScope.launch(Dispatchers.IO) {
        swipeRefreshStatus.postValue(false)
        val notesFromApi = handleNotesResponse(notesRepository.getNotesFromApi(1))
        notesPage = 1
        notesRepository.deleteAll()
        notesResponse.postValue(notesFromApi)
        notesFromApi.data?.notes?.let { notesRepository.addAll(it) }
        swipeRefreshStatus.postValue(true)
    }

    fun getNotes(firstCall: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        notesResponse.postValue(Resource.Loading())
        val notesFromApi = handleNotesResponse(notesRepository.getNotesFromApi(notesPage++))
        if (firstCall) {
            notesPage = 1
            notesRepository.deleteAll()
        }
        notesFromApi.data?.notes?.let { notesRepository.addAll(it) }
        notesResponse.postValue(notesFromApi)
    }

    private fun handleNotesResponse(response: Response<NotesResponse>): Resource<NotesResponse> {
        return if (response.isSuccessful) {
            Resource.Success(response.body()!!)
        } else {
            val notesResponse =
                Gson().fromJson(response.errorBody()!!.charStream(), NotesResponse::class.java)

            Resource.Error(null, notesResponse.message)
        }
    }

}