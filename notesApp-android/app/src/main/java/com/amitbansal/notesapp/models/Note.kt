package com.amitbansal.notesapp.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes", indices = [Index(value = ["note_id"], unique = true)])
data class Note(
    //Server id
    val note_id: Int?,

    val created_at: Long?,
    val public_url: String?,
    val text: String?,
    val title: String,
    val updated_at: Long?,
    val username: String?,

    //Local
    val sync: Boolean = false
) : Serializable {

    constructor(title: String, text: String?, sync: Boolean) : this(
        null,
        null,
        null,
        text,
        title,
        (System.currentTimeMillis() / 1000),
        null,
        sync
    )

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}