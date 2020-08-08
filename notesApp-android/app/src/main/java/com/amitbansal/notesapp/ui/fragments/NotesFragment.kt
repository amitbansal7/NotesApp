package com.amitbansal.notesapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.adapters.NotesAdapter
import com.amitbansal.notesapp.ui.viewmodels.NotesViewModel
import com.amitbansal.notesapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notes.*

@AndroidEntryPoint
class NotesFragment : Fragment(R.layout.fragment_notes) {
    private val notesViewModel: NotesViewModel by viewModels()

    lateinit var notesAdapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecylerView()
        notesViewModel.getNotes()

        notesViewModel.notes.observe(viewLifecycleOwner, Observer {
            notesAdapter.differ.submitList(it)
        })

        btnGetNotes.setOnClickListener {
            notesViewModel.getNotes()
        }

        notesViewModel.notesResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    spinner.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    spinner.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    spinner.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    private fun setupRecylerView() {
        notesAdapter = NotesAdapter()
        rvNotes.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}