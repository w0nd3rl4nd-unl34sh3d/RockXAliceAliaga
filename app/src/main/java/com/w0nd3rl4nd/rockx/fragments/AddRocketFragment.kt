package com.w0nd3rl4nd.rockx.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.w0nd3rl4nd.rockx.MainActivity
import com.w0nd3rl4nd.rockx.R
import com.w0nd3rl4nd.rockx.dbHandling.AppDatabase
import com.w0nd3rl4nd.rockx.dbHandling.DimensionEntity
import com.w0nd3rl4nd.rockx.dbHandling.RocketEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddRocketFragment : Fragment(R.layout.fragment_add_rocket) {

    private lateinit var nameEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var companyEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        countryEditText = view.findViewById(R.id.countryEditText)
        companyEditText = view.findViewById(R.id.companyEditText)
        saveButton = view.findViewById(R.id.saveButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val country = countryEditText.text.toString()
            val company = companyEditText.text.toString()

            if (name.isNotEmpty() && country.isNotEmpty() && company.isNotEmpty()) {
                val dimensionEntity = DimensionEntity(
                    heightMeters = 0.0, 
                    diameterMeters = 0.0 
                )

                lifecycleScope.launch {
                    val dimensionId = AppDatabase.getInstance(requireContext()).dimensionDao().insertDimension(dimensionEntity)

                    val rocket = RocketEntity(
                        name = name,
                        type = "Custom",  
                        active = true,    
                        costPerLaunch = 0L, 
                        successRatePct = 0, 
                        country = country,
                        company = company,
                        wikipedia = "", 
                        description = "", 
                        dimensionId = dimensionId 
                    )

                    AppDatabase.getInstance(requireContext()).rocketDao().insertRocket(rocket)

                    lifecycleScope.launch {
                        (activity as MainActivity).replaceFragment(RocketsRecyclerFragment())
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Missing fields", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            lifecycleScope.launch {
                (activity as MainActivity).replaceFragment(RocketsRecyclerFragment())
            }
        }
    }
}