package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

/**
 * Product with measurement junction entity.
 *
 * One product can have many measurements.
 *
 * @see ProductWeightMeasurementJunction
 */
data class ProductWithWeightMeasurementEntity(
    @Embedded
    val weightMeasurement: WeightMeasurementEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
