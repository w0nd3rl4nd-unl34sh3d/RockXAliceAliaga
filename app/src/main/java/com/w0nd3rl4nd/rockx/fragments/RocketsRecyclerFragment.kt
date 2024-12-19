package com.w0nd3rl4nd.rockx.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.w0nd3rl4nd.rockx.MainActivity
import com.w0nd3rl4nd.rockx.R
import com.w0nd3rl4nd.rockx.dbHandling.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.widget.SearchView

class RocketsRecyclerFragment : Fragment(R.layout.fragment_rockets_recycler) {

    private lateinit var rocketAdapter: RocketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        (activity as AppCompatActivity).supportActionBar?.title = "Rockets"

        (activity as AppCompatActivity).supportActionBar?.show()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewRockets)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        rocketAdapter = RocketAdapter { rocketName ->
            val fragment = RocketDetailFragment().apply {
                arguments = Bundle().apply { putString("rocketName", rocketName) }
            }
            (activity as MainActivity).replaceFragment(fragment)
        }
        recyclerView.adapter = rocketAdapter

        fetchRocketsAndDisplay()
    }

    private fun fetchRocketsAndDisplay() {
        val database = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val rocketEntities = database.rocketDao().getAllRockets()
            withContext(Dispatchers.Main) {
                rocketAdapter.submitList(rocketEntities)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.menu_rockets, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search Rockets"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterRockets(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterRockets(it) }
                return true
            }
        })
    }

    private fun filterRockets(query: String) {
        val database = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredRockets = database.rocketDao().searchRockets("%$query%")
            withContext(Dispatchers.Main) {
                rocketAdapter.submitList(filteredRockets)
            }
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        Log.d("RocketMenu", "Selected item ID: ${menuItem.itemId}")
        return when (menuItem.itemId) {
            R.id.action_add_rocket -> {
                Log.d("RocketMenu", "Pressed Add Rocket")
                (activity as MainActivity).replaceFragment(AddRocketFragment())
                true
            }
            R.id.action_close_session -> {
                Log.d("RocketMenu", "Pressed Close Session")
                (activity as MainActivity).replaceFragment(LoginFragment())
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}