package com.amitbansal.notesapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
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

    fun hasInternetConnection(): Boolean {
        val connectivityManager = App.instance.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}