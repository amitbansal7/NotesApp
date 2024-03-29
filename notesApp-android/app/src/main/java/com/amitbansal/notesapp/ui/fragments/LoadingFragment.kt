package com.amitbansal.notesapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.ui.viewmodels.AuthViewModel
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils

class LoadingFragment : Fragment(R.layout.fragment_loading) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()

        val user = Utils.getUser()
        user?.let {
            if (Utils.hasInternetConnection()) {
                authViewModel.authenticate(user)
            } else {
                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    R.id.action_loadingFragment_to_notesFragment
                )
            }
        } ?: run {
            findNavController().navigate(
                R.id.action_loadingFragment_to_loginFragment
            )
        }
    }


    private fun setObservers() {
        authViewModel.authenticateResponse.observe(this as LifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_loadingFragment_to_notesFragment)
                }
                is Resource.Error -> {
                    Utils.deleteUserFromSharedPreferences()
                    findNavController().navigate(
                        R.id.action_loadingFragment_to_loginFragment
                    )
                }
            }
        })
    }
}