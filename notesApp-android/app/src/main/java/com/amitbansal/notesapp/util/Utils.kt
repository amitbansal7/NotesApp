package com.amitbansal.notesapp.util

import androidx.preference.PreferenceManager
import com.amitbansal.notesapp.App
import com.amitbansal.notesapp.models.User
import com.google.gson.Gson

object Utils {

    private const val USER_PREF = "notes_app_preferences"
    private const val USER = "user"
    private var user: User? = null

    fun getUser(): User? {
        return user ?: getUserFromSharedPreferences().also { user = it }
    }

    fun setUserInSharedPreferences(user: User): Unit {
        this.user = user
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(App.instance)
        val editor = sharedPreference.edit()
        editor.putString(USER, Gson().toJson(user))
        editor.apply()
    }

    fun deleteUserFromSharedPreferences() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(App.instance)
        val editor = sharedPreference.edit()
        editor.putString(USER, "")
        editor.apply()
    }

    private fun getUserFromSharedPreferences(): User? {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(App.instance)
        val userString = sharedPreference.getString(USER, "")
        return if (userString.isNullOrEmpty()) {
            null
        } else {
            Gson().fromJson(userString, User::class.java)
        }
    }
}