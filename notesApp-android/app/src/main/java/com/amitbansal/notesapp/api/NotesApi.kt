package com.amitbansal.notesapp.api

import com.amitbansal.notesapp.models.NotesResponse
import com.amitbansal.notesapp.util.Utils
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface NotesApi {

    @GET("notes")
    suspend fun getAll(
        @Header("Authentication") auth: String? = Utils.getUser()?.auth_token
    ): Response<NotesResponse>
}