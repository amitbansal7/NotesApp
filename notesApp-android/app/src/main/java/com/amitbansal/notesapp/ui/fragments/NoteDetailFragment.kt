package com.amitbansal.notesapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.models.Note
import com.amitbansal.notesapp.models.NoteResponse
import com.amitbansal.notesapp.ui.viewmodels.NotesViewModel
import com.amitbansal.notesapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_detail.*

@AndroidEntryPoint
class NoteDetailFragment : Fragment(R.layout.fragment_note_detail) {
    private val args: NoteDetailFragmentArgs by navArgs()

    private val notesViewModel: NotesViewModel by viewModels()

    lateinit var note: Note

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.note = args.note
        updateNoteOnUi()

        setObservers()

        btnCreate.setOnClickListener {
            updateNote()
        }

        btnShare.setOnClickListener {
            shareNote()
        }

        btnMakePrivate.setOnClickListener{
            makePrivate()
        }
    }

    private fun makePrivate(){
        notesViewModel.makeNotePrivate(note)
    }

    private fun shareNote() {
        if (note.public_url.isNullOrEmpty()) {
            notesViewModel.makeNotePublic(note)
        } else {
            notesViewModel.makePublicNoteResponse.postValue(
                Resource.Success(
                    NoteResponse(note, "")
                )
            )
        }

    }

    private fun updateNote() {
        note = note.copy(text = tvNoteText.text.toString(), title = tvNoteTitle.text.toString())
        notesViewModel.updateNote(note)
    }

    private fun setObservers() {
        notesViewModel.updateNoteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(activity, it.data?.message, Toast.LENGTH_SHORT).show()
                    this.note = it.data?.note!!
                    updateNoteOnUi()
                    spinner.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    Toast.makeText(activity, it.data?.message, Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.INVISIBLE
                }
                is Resource.Loading -> {
                    spinner.visibility = View.VISIBLE
                }
            }
        })

        notesViewModel.makePublicNoteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(activity, it.data?.message, Toast.LENGTH_SHORT).show()
                    this.note = it.data?.note!!
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, note.public_url)
                    }
                    updateNoteOnUi()
                    startActivity(Intent.createChooser(shareIntent, "Share To:"))
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

    private fun updateNoteOnUi() {
        tvNoteTitle.text = Editable.Factory.getInstance().newEditable(note.title)
        tvNoteText.text = Editable.Factory.getInstance().newEditable(note.text)
        if (!note.public_url.isNullOrEmpty()) {
            btnMakePrivate.visibility = View.VISIBLE
        } else {
            btnMakePrivate.visibility = View.INVISIBLE
        }
    }
}