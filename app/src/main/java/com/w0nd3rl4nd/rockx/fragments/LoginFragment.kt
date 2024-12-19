package com.w0nd3rl4nd.rockx.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.w0nd3rl4nd.rockx.MainActivity
import com.w0nd3rl4nd.rockx.R
import com.w0nd3rl4nd.rockx.dbHandling.AppDatabase
import com.w0nd3rl4nd.rockx.dbHandling.RestHandling
import com.w0nd3rl4nd.rockx.dbHandling.SaveRockets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText = view.findViewById<EditText>(R.id.etUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.etPassword)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username == "admin" && password == "admin1234") {

                lifecycleScope.launch {
                    val rocketDao = AppDatabase.getInstance(requireContext()).rocketDao()
                    val rockets = rocketDao.getAllRockets()

                    if (rockets.isNotEmpty()) {
                        Log.d("RocketDatabase", "Rockets already exist in the database. Skipping API fetch.")
                        (activity as MainActivity).replaceFragment(RocketsRecyclerFragment())
                    } else {
                        Log.d("RocketDatabase", "Database is empty. Fetching rockets from API.")
                        val restHandling = RestHandling()
                        restHandling.getAllRockets { rocketList ->
                            if (rocketList.isNotEmpty()) {
                                for (rocket in rocketList) {
                                    Log.d("Rocket", "Name: ${rocket["name"]}")
                                }
                                context?.let {
                                    val saveRockets = SaveRockets(it, lifecycleScope)
                                    saveRockets.saveToDatabase(rocketList)
                                }
                                lifecycleScope.launch {
                                    delay(500)
                                    (activity as MainActivity).replaceFragment(
                                        RocketsRecyclerFragment()
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Can't fetch the data or find it locally",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
                passwordEditText.text.clear()
            }
        }
    }
}