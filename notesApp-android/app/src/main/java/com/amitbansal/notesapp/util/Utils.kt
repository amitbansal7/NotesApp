package com.amitbansal.notesapp.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.amitbansal.notesapp.models.responses.User
import com.google.gson.Gson

object Utils {

    private const val USER_PREF = "notes_app_preferences"
    private const val USER = "user"

    fun getUserFromSharedPreferences(context: Context): User? {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val userString = sharedPreference.getString(USER, "")
        if (userString.isNullOrEmpty()) return null
        return Gson().fromJson(userString, User::class.java)
    }

    fun setUserInSharedPreferences(context: Context, user: User): Unit {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreference.edit()
        editor.putString(USER, Gson().toJson(user))
        editor.apply()
    }

    fun deleteUserFromSharedPreferences(context: Context){
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreference.edit()
        editor.putString(USER, "")
        editor.apply()
    }
}