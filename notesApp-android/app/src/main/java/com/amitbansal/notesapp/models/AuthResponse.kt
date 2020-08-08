package com.amitbansal.notesapp.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("data")
    val user: User?,

    val message: String
)