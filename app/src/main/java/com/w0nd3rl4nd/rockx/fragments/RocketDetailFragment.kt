package com.w0nd3rl4nd.rockx.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.w0nd3rl4nd.rockx.MainActivity
import com.w0nd3rl4nd.rockx.R
import com.w0nd3rl4nd.rockx.dbHandling.AppDatabase
import com.w0nd3rl4nd.rockx.dbHandling.DimensionEntity
import com.w0nd3rl4nd.rockx.dbHandling.RocketEntity
import com.w0nd3rl4nd.rockx.dbHandling.RocketWithDimension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RocketDetailFragment : Fragment(R.layout.fragment_rocket_detail) {

    private var rocketName: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rocketName = arguments?.getString("rocketName")
        if (rocketName == null) {
            return
        }

        val tvRocketName = view.findViewById<TextView>(R.id.tvRocketName)
        val tvRocketType = view.findViewById<TextView>(R.id.tvRocketType)
        val tvCountry = view.findViewById<TextView>(R.id.tvCountry)
        val tvCompany = view.findViewById<TextView>(R.id.tvCompany)
        val tvCostPerLaunch = view.findViewById<TextView>(R.id.tvCostPerLaunch)
        val tvSuccessRate = view.findViewById<TextView>(R.id.tvSuccessRate)
        val tvWikipedia = view.findViewById<TextView>(R.id.tvWikipedia)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val tvDimensions = view.findViewById<TextView>(R.id.tvDimensions)
        val btnEditRocket = view.findViewById<Button>(R.id.btnEditRocket)
        val btnSaveRocket = view.findViewById<Button>(R.id.btnSaveRocket)
        val btnDeleteRocket = view.findViewById<Button>(R.id.btnDeleteRocket)
        val etRocketName = view.findViewById<EditText>(R.id.etRocketName)
        val etRocketType = view.findViewById<EditText>(R.id.etRocketType)
        val etCountry = view.findViewById<EditText>(R.id.etCountry)
        val etCompany = view.findViewById<EditText>(R.id.etCompany)
        val etCostPerLaunch = view.findViewById<EditText>(R.id.etCostPerLaunch)
        val etSuccessRate = view.findViewById<EditText>(R.id.etSuccessRate)
        val etWikipedia = view.findViewById<EditText>(R.id.etWikipedia)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etDimensions = view.findViewById<EditText>(R.id.etDimensions)

        val database = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val rocketWithDimension: RocketWithDimension? =
                database.rocketDao().getRocketWithDimensionByName(rocketName!!)

            withContext(Dispatchers.Main) {
                if (rocketWithDimension == null) {
                    tvRocketName.text = "Rocket not found"
                } else {

                    val rocket = rocketWithDimension.rocket
                    val dimension = rocketWithDimension.dimension

                    tvRocketName.text = rocket.name
                    tvRocketType.text = "Type: ${rocket.type}"
                    tvCountry.text = "Country: ${rocket.country}"
                    tvCompany.text = "Company: ${rocket.company}"
                    tvCostPerLaunch.text = "Cost per launch: $${rocket.costPerLaunch}M"
                    tvSuccessRate.text = "Success Rate: ${rocket.successRatePct}%"
                    tvWikipedia.text = "Wikipedia"
                    tvDescription.text = rocket.description
                    tvDimensions.text = "Dimensions: Height ${dimension.heightMeters}m, Diameter ${dimension.diameterMeters}m"

                    btnEditRocket.visibility = View.VISIBLE
                    btnDeleteRocket.visibility = View.VISIBLE

                    setupClickListeners(tvWikipedia, rocket.wikipedia, tvCountry, rocket.country)

                    btnDeleteRocket.setOnClickListener {
                        showDeleteConfirmationDialog(rocket)
                    }

                    btnEditRocket.setOnClickListener {
                        btnEditRocket.visibility = View.GONE
                        btnDeleteRocket.visibility = View.GONE

                        toggleEditMode(true)

                        etRocketName.setText(rocket.name)
                        etRocketType.setText(rocket.type)
                        etCountry.setText(rocket.country)
                        etCompany.setText(rocket.company)
                        etCostPerLaunch.setText(rocket.costPerLaunch.toString())
                        etSuccessRate.setText(rocket.successRatePct.toString())
                        etWikipedia.setText(rocket.wikipedia)
                        etDescription.setText(rocket.description)
                        etDimensions.setText("${dimension.heightMeters}m x ${dimension.diameterMeters}m")
                    }

                    btnSaveRocket.setOnClickListener {
                        val rocketName = etRocketName.text.toString()
                        val rocketType = etRocketType.text.toString()
                        val country = etCountry.text.toString()
                        val company = etCompany.text.toString()
                        val costPerLaunch = etCostPerLaunch.text.toString().toLongOrNull() ?: 0L
                        val successRate = etSuccessRate.text.toString().toIntOrNull() ?: 0
                        val wikipedia = etWikipedia.text.toString()
                        val description = etDescription.text.toString()

                        val dimensionText = etDimensions.text.toString().split("x")
                        val heightMeters = dimensionText.getOrNull(0)?.trim()?.removeSuffix("m")?.toDoubleOrNull() ?: 0.0
                        val diameterMeters = dimensionText.getOrNull(1)?.trim()?.removeSuffix("m")?.toDoubleOrNull() ?: 0.0

                        lifecycleScope.launch(Dispatchers.IO) {
                            val database = AppDatabase.getInstance(requireContext())

                            val existingDimension = database.dimensionDao().getDimensionByHeightAndDiameter(heightMeters, diameterMeters)
                            val dimensionId: Long

                            if (existingDimension == null) {
                                val dimension = DimensionEntity(heightMeters = heightMeters, diameterMeters = diameterMeters)
                                dimensionId = database.dimensionDao().insertDimension(dimension)
                                Log.d("SaveRockets", "New dimension inserted with ID: $dimensionId")
                            } else {
                                existingDimension.heightMeters = heightMeters
                                existingDimension.diameterMeters = diameterMeters
                                database.dimensionDao().updateDimension(existingDimension)
                                dimensionId = existingDimension.id
                                Log.d("SaveRockets", "Updated existing dimension with ID: $dimensionId")
                            }

                            val rocketEntity = RocketEntity(
                                name = rocketName,
                                type = rocketType,
                                active = true,
                                costPerLaunch = costPerLaunch,
                                successRatePct = successRate,
                                country = country,
                                company = company,
                                wikipedia = wikipedia,
                                description = description,
                                dimensionId = dimensionId
                            )

                            database.rocketDao().updateRocket(rocketEntity)
                            Log.d("SaveRockets", "Inserted rocket: $rocketName")

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Rocket saved successfully!", Toast.LENGTH_SHORT).show()

                                toggleEditMode(false)
                            }
                        }

                        btnEditRocket.visibility = View.VISIBLE
                        btnDeleteRocket.visibility = View.VISIBLE
                        /* TODO("Self update text") */
                    }

                }
            }
        }
    }


    private fun toggleEditMode(isEditing: Boolean) {
        val editTexts = listOf(
            view?.findViewById<EditText>(R.id.etRocketType),
            view?.findViewById<EditText>(R.id.etCountry),
            view?.findViewById<EditText>(R.id.etCompany),
            view?.findViewById<EditText>(R.id.etCostPerLaunch),
            view?.findViewById<EditText>(R.id.etSuccessRate),
            view?.findViewById<EditText>(R.id.etWikipedia),
            view?.findViewById<EditText>(R.id.etDescription),
            view?.findViewById<EditText>(R.id.etDimensions)
        )
        val textViews = listOf(
            view?.findViewById<TextView>(R.id.tvRocketType),
            view?.findViewById<TextView>(R.id.tvCountry),
            view?.findViewById<TextView>(R.id.tvCompany),
            view?.findViewById<TextView>(R.id.tvCostPerLaunch),
            view?.findViewById<TextView>(R.id.tvSuccessRate),
            view?.findViewById<TextView>(R.id.tvWikipedia),
            view?.findViewById<TextView>(R.id.tvDescription),
            view?.findViewById<TextView>(R.id.tvDimensions)
        )
        val saveButton = view?.findViewById<Button>(R.id.btnSaveRocket)

        editTexts.forEach { it?.visibility = if (isEditing) View.VISIBLE else View.GONE }
        textViews.forEach { it?.visibility = if (isEditing) View.GONE else View.VISIBLE }
        saveButton?.visibility = if (isEditing) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners(
        tvWikipedia: TextView,
        wikipediaUrl: String,
        tvCountry: TextView,
        country: String
    ) {

        tvWikipedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }

        tvCountry.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=$country")
            }
            startActivity(intent)
        }
    }

    private fun showDeleteConfirmationDialog(rocket: RocketEntity) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete this rocket?")
            .setPositiveButton("Yes") { _, _ ->
                deleteRocket(rocket)
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun deleteRocket(rocket: RocketEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getInstance(requireContext())
            database.rocketDao().deleteRocket(rocket)

            withContext(Dispatchers.Main) {
                (activity as MainActivity).replaceFragment(RocketsRecyclerFragment())
            }
        }
    }

}