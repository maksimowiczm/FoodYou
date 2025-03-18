package com.maksimowiczm.foodyou.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithWeightMeasurementEntity(
    @Embedded
    val weightMeasurement: WeightMeasurementEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
