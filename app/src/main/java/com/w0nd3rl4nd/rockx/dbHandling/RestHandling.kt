package com.w0nd3rl4nd.rockx.dbHandling

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class RocketData(
    val name: String,
    val type: String,
    val active: Boolean,
    @SerializedName("cost_per_launch") val costPerLaunch: Long,  // Explicit mapping
    @SerializedName("success_rate_pct") val successRatePct: Int,  // Explicit mapping
    val country: String,
    val company: String,
    val wikipedia: String,
    val description: String,
    val height: Dimension,
    val diameter: Dimension
)

data class Dimension(
    val meters: Double?,
    val feet: Double?
)

interface SpaceXApiService {
    @GET("rockets")
    fun getRockets(): Call<List<RocketData>>
}

class RestHandling {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spacexdata.com/v4/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: SpaceXApiService = retrofit.create(SpaceXApiService::class.java)

    fun getAllRockets(callback: (List<Map<String, Any>>) -> Unit) {
        val call = apiService.getRockets()

        call.enqueue(object : Callback<List<RocketData>> {
            override fun onResponse(call: Call<List<RocketData>>, response: Response<List<RocketData>>) {
                if (response.isSuccessful) {
                    val rawData = response.body()?.joinToString(separator = "\n") { rocket ->
                        "Rocket name: ${rocket.name}, cost_per_launch: ${rocket.costPerLaunch}, success_rate_pct: ${rocket.successRatePct}"
                    }
                    Log.d("RestRocket", "Raw Data: $rawData")
                    val rockets = response.body() ?: emptyList()
                    val rocketDataList = rockets.map { rocket ->
                        mapOf<String, Any>(
                            "name" to rocket.name,
                            "type" to rocket.type,
                            "active" to rocket.active,
                            "cost_per_launch" to rocket.costPerLaunch,
                            "success_rate_pct" to rocket.successRatePct,
                            "country" to rocket.country,
                            "company" to rocket.company,
                            "wikipedia_link" to rocket.wikipedia,
                            "description" to rocket.description,
                            "height_meters" to (rocket.height.meters ?: 0.0),
                            "diameter_meters" to (rocket.diameter.meters ?: 0.0)
                        )
                    }
                    callback(rocketDataList)
                } else {
                    Log.e("Rocket", "Error: ${response.errorBody()?.string()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<RocketData>>, t: Throwable) {
                Log.e("RestHandling", "Failure: ${t.message}")
                callback(emptyList())
            }
        })
    }
}