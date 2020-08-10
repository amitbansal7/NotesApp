package com.amitbansal.notesapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.util.Constants.NOTE_ID
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.note_item.view.*

class NoteDetailFragment : Fragment(R.layout.fragment_note_detail) {
    private val args: NoteDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val note = args.note
        tvNoteTitle.text = Editable.Factory.getInstance().newEditable(note.title)
        tvNoteText.text = Editable.Factory.getInstance().newEditable(note.text)
    }
}