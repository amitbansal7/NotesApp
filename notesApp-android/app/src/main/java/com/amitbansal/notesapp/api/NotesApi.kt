package com.amitbansal.notesapp.api

import com.amitbansal.notesapp.models.NoteResponse
import com.amitbansal.notesapp.models.NotesResponse
import com.amitbansal.notesapp.util.Utils
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {

    @GET("notes")
    suspend fun getAll(
        @Query("page")
        page: Int,

        @Header("Authentication") auth: String? = Utils.getUser()?.auth_token
    ): Response<NotesResponse>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id")
        id: Int,

        @Query("title")
        title: String,

        @Query("text")
        text: String,

        @Header("Authentication") auth: String? = Utils.getUser()?.auth_token
    ): Response<NoteResponse>

    @PUT("notes/public/{id}")
    suspend fun makePublic(
        @Path("id")
        id: Int,

        @Header("Authentication") auth: String? = Utils.getUser()?.auth_token
    ): Response<NoteResponse>

    @PUT("notes/private/{id}")
    suspend fun makePrivate(
        @Path("id")
        id: Int,

        @Header("Authentication") auth: String? = Utils.getUser()?.auth_token
    ): Response<NoteResponse>
}