package com.w0nd3rl4nd.rockx.dbHandling

    import androidx.room.Entity
    import androidx.room.PrimaryKey
    import androidx.room.ColumnInfo
    import androidx.room.ForeignKey

    @Entity(tableName = "dimension")
    data class DimensionEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "height_meters") var heightMeters: Double?,
        @ColumnInfo(name = "diameter_meters") var diameterMeters: Double?,
    )

    @Entity(
        tableName = "rocket",
        foreignKeys = [ForeignKey(
            entity = DimensionEntity::class,
            parentColumns = ["id"],
            childColumns = ["dimension_id"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class RocketEntity(
        @PrimaryKey @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "type") val type: String,
        @ColumnInfo(name = "active") val active: Boolean,
        @ColumnInfo(name = "costPerLaunch") val costPerLaunch: Long,
        @ColumnInfo(name = "successRatePct") val successRatePct: Int,
        @ColumnInfo(name = "country") val country: String,
        @ColumnInfo(name = "company") val company: String,
        @ColumnInfo(name = "wikipedia") val wikipedia: String,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "dimension_id") val dimensionId: Long
    )
