package com.maksimowiczm.foodyou.feature.addfood.database

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductEntity

data class ProductWithWeightMeasurementEntity(
    @Embedded
    val weightMeasurement: WeightMeasurementEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
