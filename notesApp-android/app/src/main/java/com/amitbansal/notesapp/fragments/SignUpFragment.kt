package com.amitbansal.notesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.amitbansal.notesapp.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLogin.setOnClickListener{
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }
}