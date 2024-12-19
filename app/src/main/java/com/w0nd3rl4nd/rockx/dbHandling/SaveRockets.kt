package com.w0nd3rl4nd.rockx.dbHandling

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveRockets(private val context: Context, private val lifecycleScope: LifecycleCoroutineScope) {

    fun saveToDatabase(rocketList: List<Map<String, Any>>) {
        val database = AppDatabase.getInstance(context)

        Log.d("SaveRockets", "Rocket list size: ${rocketList.size}")

        lifecycleScope.launch(Dispatchers.IO) {
            for (rocketMap in rocketList) {
                Log.d("SaveRockets", "Rocket Data: $rocketMap")

                val heightMeters = rocketMap["height_meters"] as? Double ?: 0.0
                val diameterMeters = rocketMap["diameter_meters"] as? Double ?: 0.0

                val existingDimension = database.dimensionDao()
                    .getDimensionByHeightAndDiameter(heightMeters, diameterMeters)

                val dimensionId: Long
                if (existingDimension == null) {
                    val dimension = DimensionEntity(
                        heightMeters = heightMeters,
                        diameterMeters = diameterMeters
                    )
                    dimensionId = database.dimensionDao().insertDimension(dimension)
                    Log.d("SaveRockets", "New dimension inserted with ID: $dimensionId")
                } else {
                    dimensionId = existingDimension.id
                    Log.d("SaveRockets", "Reusing existing dimension with ID: $dimensionId")
                }

                val rocketEntity = RocketEntity(
                    name = rocketMap["name"] as? String ?: "Unknown",
                    type = rocketMap["type"] as? String ?: "Unknown",
                    active = rocketMap["active"] as? Boolean ?: false,
                    costPerLaunch = rocketMap["cost_per_launch"] as? Long ?: 0L,
                    successRatePct = rocketMap["success_rate_pct"] as? Int ?: 0,
                    country = rocketMap["country"] as? String ?: "Unknown",
                    company = rocketMap["company"] as? String ?: "Unknown",
                    wikipedia = rocketMap["wikipedia_link"] as? String ?: "",
                    description = rocketMap["description"] as? String ?: "",
                    dimensionId = dimensionId
                )

                database.rocketDao().insertRocket(rocketEntity)
                Log.d("SaveRockets", "Inserted rocket: ${rocketEntity.name}")
            }

            launch(Dispatchers.Main) {
                Toast.makeText(context, "Rockets saved to database!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
