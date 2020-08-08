package com.amitbansal.notesapp.api

import com.amitbansal.notesapp.models.responses.AuthResponse
import com.amitbansal.notesapp.util.Constants.SECRET_KEY
import com.amitbansal.notesapp.util.Constants.SECRET_VALUE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @Headers("$SECRET_KEY: $SECRET_VALUE")
    @GET("o/user/signin")
    suspend fun signin(
        @Query("username_or_email")
        emailOrUserName: String,

        @Query("password")
        password: String

    ): Response<AuthResponse>

    @Headers("$SECRET_KEY: $SECRET_VALUE")
    @POST("o/user/signup")
    suspend fun signup(
        @Query("email")
        email: String,

        @Query("username")
        username: String? = null,

        @Query("password")
        password: String,

        @Query("phone_number")
        phone_number: String? = null

    ): Response<AuthResponse>

    @Headers("$SECRET_KEY: $SECRET_VALUE")
    @GET("o/user/auth")
    suspend fun authenticate(
        @Query("auth_token")
        authToken: String

    ): Response<AuthResponse>

}