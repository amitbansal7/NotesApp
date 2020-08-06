package com.amitbansal.notesapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amitbansal.notesapp.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSignup.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }


}