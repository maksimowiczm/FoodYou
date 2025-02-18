package com.maksimowiczm.foodyou.core.feature.addfood.database

/**
 * Product with measurement junction entity.
 *
 * One product can have many measurements.
 *
 * @see ProductWithWeightMeasurementEntity
 */
data class ProductWeightMeasurementJunction(
    val productId: Long,
    val measurementId: Long?
)
