package com.amitbansal.notesapp.models

import com.google.gson.annotations.SerializedName

data class NoteResponse (
    @SerializedName("data")
    var note: Note,
    val message: String
)
