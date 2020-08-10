package com.amitbansal.notesapp.models

import com.amitbansal.notesapp.models.Note
import com.google.gson.annotations.SerializedName

data class NotesResponse(
    @SerializedName("data")
    var notes: MutableList<Note>,
    val message: String
)