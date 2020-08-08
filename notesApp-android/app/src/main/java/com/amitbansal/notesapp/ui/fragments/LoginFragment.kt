package com.amitbansal.notesapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.ui.viewmodels.AuthViewModel
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()

        tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        btnLogin.setOnClickListener {
            tryToLogin()
        }
    }

    private fun tryToLogin() {
        val username = ivUsername.text.toString()
        val password = ivPassword.text.toString()
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Username or password cannot be empty", Toast.LENGTH_SHORT)
                .show()

        } else {
            authViewModel.login(username, password)
        }
    }

    private fun setObservers() {
        authViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(activity, it.data!!.message, Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.INVISIBLE
                    Utils.setUserInSharedPreferences(it.data.user!!)
                    findNavController().navigate(R.id.action_loginFragment_to_notesFragment)
                }
                is Resource.Error -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.INVISIBLE
                    btnLogin.isClickable = true
                }
                is Resource.Loading -> {
                    btnLogin.isClickable = false
                    spinner.visibility = View.VISIBLE
                }
            }

        })
    }
}