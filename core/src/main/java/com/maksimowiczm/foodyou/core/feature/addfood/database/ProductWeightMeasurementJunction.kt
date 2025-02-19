package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.room.ColumnInfo

/**
 * Product with measurement junction entity.
 *
 * One product can have many measurements.
 *
 * @see ProductWithWeightMeasurementEntity
 */
data class ProductWeightMeasurementJunction(
    val productId: Long,
    val measurementId: Long?,

    /**
     * @see WeightMeasurementEntity.rank
     */
    @ColumnInfo(name = "realRank")
    val rank: Float
) {
    companion object {
        const val DEFAULT_RANK = 1f
    }
}
