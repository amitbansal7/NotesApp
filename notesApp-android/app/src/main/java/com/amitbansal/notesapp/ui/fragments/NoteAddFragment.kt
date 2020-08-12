package com.amitbansal.notesapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.ui.viewmodels.NotesViewModel
import com.amitbansal.notesapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_add.*

@AndroidEntryPoint
class NoteAddFragment : Fragment(R.layout.fragment_note_add) {
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()

        btnCreate.setOnClickListener {
            tryToCreateNote()
        }
    }

    private fun tryToCreateNote() {
        val title = tvNoteTitle.text.toString()
        if (title.isNullOrEmpty()) {
            Toast.makeText(activity, "Title is blank", Toast.LENGTH_SHORT).show()
            return
        }
        val text = tvNoteText.text.toString()

        notesViewModel.createNote(title, text)
    }

    private fun setObservers() {
        notesViewModel.createNoteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(activity, it.data?.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    spinner.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.INVISIBLE
                }
                is Resource.Loading -> {
                    spinner.visibility = View.VISIBLE
                }
            }
        })
    }
}