package com.amitbansal.notesapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amitbansal.notesapp.api.RetrofitInstance
import com.amitbansal.notesapp.models.AuthResponse
import com.amitbansal.notesapp.models.User
import com.amitbansal.notesapp.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel @ViewModelInject constructor() : ViewModel() {

    val authenticateResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val loginResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val signupResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()


    fun login(username: String, password: String) = viewModelScope.launch {
        loginResponse.postValue(Resource.Loading())
        loginResponse.postValue(
            handleAuthResponse(
                RetrofitInstance.authApi.signin(
                    username, password
                )
            )
        )
    }

    fun signup(email: String, password: String) = viewModelScope.launch {
        signupResponse.postValue(Resource.Loading())
        signupResponse.postValue(
            handleAuthResponse(
                RetrofitInstance.authApi.signup(
                    email = email, password = password
                )
            )
        )
    }

    fun authenticate(user: User) = viewModelScope.launch {
        authenticateResponse.postValue(
            handleAuthResponse(
                RetrofitInstance.authApi.authenticate(
                    user.auth_token
                )
            )
        )
    }

    private fun handleAuthResponse(response: Response<AuthResponse>): Resource<AuthResponse> {
        return if (response.isSuccessful) {
            Resource.Success(response.body()!!)
        } else {
            val authResponse =
                Gson().fromJson(response.errorBody()!!.charStream(), AuthResponse::class.java)

            Resource.Error(authResponse, authResponse.message)
        }
    }

}