package com.amitbansal.notesapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.ui.viewmodels.AuthViewModel
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setObservers()

        val user = Utils.getUserFromSharedPreferences(this)
        user?.let {
            authViewModel.authenticate(user)
        } ?: run {
            findNavController(R.id.navHostFragment).navigate(
                R.id.action_login_fragment
            )
        }
    }

    private fun setObservers() {
        authViewModel.authenticateResponse.observe(this as LifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    findNavController(R.id.navHostFragment).navigate(R.id.action_notes_fragment)
                }
                is Resource.Error -> {
                    Utils.deleteUserFromSharedPreferences(this)
                    findNavController(R.id.navHostFragment).navigate(
                        R.id.action_login_fragment
                    )
                }
            }
        })
    }
}