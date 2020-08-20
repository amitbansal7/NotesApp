package com.amitbansal.notesapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amitbansal.notesapp.R
import com.amitbansal.notesapp.adapters.NotesAdapter
import com.amitbansal.notesapp.ui.viewmodels.NotesViewModel
import com.amitbansal.notesapp.util.Resource
import com.amitbansal.notesapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notes.*


@AndroidEntryPoint
class NotesFragment : Fragment(R.layout.fragment_notes) {
    private val notesViewModel: NotesViewModel by viewModels()

    lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        setupObservers()

        notesViewModel.notes.observe(viewLifecycleOwner, Observer {
            notesAdapter.differ.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_noteAddFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemLogout -> {
                Toast.makeText(activity, "Logging out", Toast.LENGTH_SHORT).show()
                logoutUser()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser(){
        Utils.deleteUserFromSharedPreferences()
        Utils.deleteUser()
        notesViewModel.deleteAllNotes()
        findNavController().navigate(R.id.action_notesFragment_to_loginFragment)
    }

    private fun setupObservers() {
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

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter()

        notesAdapter.setOnNotesClickListener {
            val args = Bundle().apply {
                putSerializable("note", it)
            }

            findNavController().navigate(R.id.action_notesFragment_to_noteDetailFragment, args)
        }

        var loading = true
        var pastVisiblesItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int

        val mLayoutManager = LinearLayoutManager(activity)

        rvNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.childCount
                    totalItemCount = mLayoutManager.itemCount
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            Log.v("...", "Last Item Wow !")
                            // Do pagination.. i.e. fetch new data
                            loading = true
                            notesViewModel.getNotes()
                        }
                    }
                }
            }
        })

        rvNotes.apply {
            adapter = notesAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun setupSwipeToRefresh() {
        swipeContainer.setOnRefreshListener {
            notesViewModel.refreshNotes()
        }
        swipeContainer.setColorSchemeResources(
            R.color.strokeColor
        )

        notesViewModel.swipeRefreshStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    swipeContainer.isRefreshing = false
                }
                is Resource.Error -> {
                    swipeContainer.isRefreshing = false
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}