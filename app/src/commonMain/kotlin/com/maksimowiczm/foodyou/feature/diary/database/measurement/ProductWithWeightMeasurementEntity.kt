package com.maksimowiczm.foodyou.feature.diary.database.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity

data class ProductWithWeightMeasurementEntity(
    @Embedded
    val weightMeasurement: WeightMeasurementEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
