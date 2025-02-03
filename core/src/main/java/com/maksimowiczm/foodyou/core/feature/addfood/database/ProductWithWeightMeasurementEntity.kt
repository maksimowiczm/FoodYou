package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

data class ProductWithWeightMeasurementEntity(
    @Embedded
    val weightMeasurement: WeightMeasurementEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
