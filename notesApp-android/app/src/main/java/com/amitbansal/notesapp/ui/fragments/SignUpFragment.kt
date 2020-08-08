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
import kotlinx.android.synthetic.main.fragment_sign_up.*

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        btnSignup.setOnClickListener {
            tryToSignup()
        }
    }

    private fun tryToSignup() {
        val email = ivEmail.text.toString()
        val password = ivPassword.text.toString()
        val confirmPassword = ivConfirmPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Username or password cannot be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (password != confirmPassword) {
            Toast.makeText(activity, "Password Doesn't match", Toast.LENGTH_SHORT)
                .show()
        } else {
            authViewModel.signup(email, password)
        }
    }

    private fun setObservers() {
        authViewModel.signupResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(activity, it.data!!.message, Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.INVISIBLE
                    btnSignup.isClickable = true
                    Utils.setUserInSharedPreferences(it.data.user!!)
                    findNavController().navigate(R.id.action_signUpFragment_to_notesFragment)
                }
                is Resource.Error -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.INVISIBLE
                    btnSignup.isClickable = true
                }
                is Resource.Loading -> {
                    btnSignup.isClickable = false
                    spinner.visibility = View.VISIBLE
                }
            }

        })
    }
}