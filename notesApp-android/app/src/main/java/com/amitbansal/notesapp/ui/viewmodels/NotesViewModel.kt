package com.amitbansal.notesapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amitbansal.notesapp.api.RetrofitInstance
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

    var notes: LiveData<List<Note>> = notesRepository.getAllDbNotes()

    fun getNotes() = viewModelScope.launch(Dispatchers.IO) {
        notesResponse.postValue(Resource.Loading())
        notesResponse.postValue(handleNotesResponse(RetrofitInstance.notesApi.getAll()))
    }

    private suspend fun handleNotesResponse(response: Response<NotesResponse>): Resource<NotesResponse> {
        return if (response.isSuccessful) {
            notesRepository.deleteAll()
            notesRepository.addAll(response.body()!!.notes)
            Resource.Success(response.body()!!)

        } else {
            val notesResponse =
                Gson().fromJson(response.errorBody()!!.charStream(), NotesResponse::class.java)

            Resource.Error(null, notesResponse.message)
        }
    }

}