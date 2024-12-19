package com.w0nd3rl4nd.rockx.dbHandling

import androidx.room.*

@Dao
interface DimensionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDimension(dimension: DimensionEntity): Long

    @Update
    suspend fun updateDimension(dimension: DimensionEntity)

    @Query("SELECT * FROM dimension WHERE height_meters = :heightMeters AND diameter_meters = :diameterMeters LIMIT 1")
    fun getDimensionByHeightAndDiameter(heightMeters: Double, diameterMeters: Double): DimensionEntity?

    @Delete
    suspend fun deleteDimension(dimension: DimensionEntity)

    @Query("SELECT * FROM dimension")
    suspend fun getAllDimensions(): List<DimensionEntity> // To get all dimensions (useful for custom rocket creation)
}

@Dao
interface RocketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRocket(rocket: RocketEntity)

    @Update
    suspend fun updateRocket(rocket: RocketEntity)

    @Delete
    suspend fun deleteRocket(rocket: RocketEntity)

    @Query("SELECT * FROM rocket WHERE name = :name")
    suspend fun getRocketByName(name: String): RocketEntity?

    @Query("SELECT * FROM rocket WHERE name LIKE :query ORDER BY name ASC")
    fun searchRockets(query: String): List<RocketEntity>

    @Transaction
    @Query("SELECT * FROM rocket WHERE name = :name")
    suspend fun getRocketWithDimensionByName(name: String): RocketWithDimension?

    // New method to get all rockets (optional, useful for custom rocket creation)
    @Query("SELECT * FROM rocket")
    suspend fun getAllRockets(): List<RocketEntity>

    @Transaction
    @Query("SELECT * FROM rocket")
    suspend fun getAllRocketsWithDimensions(): List<RocketWithDimension> // Fetch rockets with their dimensions
}