package com.w0nd3rl4nd.rockx.dbHandling

import androidx.room.Embedded
import androidx.room.Relation

data class RocketWithDimension(
    @Embedded val rocket: RocketEntity,
    @Relation(
        parentColumn = "dimension_id",
        entityColumn = "id"
    )
    val dimension: DimensionEntity
)