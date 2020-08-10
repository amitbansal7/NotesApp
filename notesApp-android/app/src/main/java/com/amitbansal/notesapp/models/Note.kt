package com.amitbansal.notesapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: Int,
    val created_at: Int,
    val public_url: String?,
    val text: String?,
    val title: String,
    val updated_at: Int,
    val username: String?,

    //Local
    val sync: Boolean = false
): Serializable