package com.amitbansal.notesapp.models.responses

data class User(
    val auth_token: String,
    val email: String,
    val name: String?,
    val phone_number: String?,
    val username: String?
)