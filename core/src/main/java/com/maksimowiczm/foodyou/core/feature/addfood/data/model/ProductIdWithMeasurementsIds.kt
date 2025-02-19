package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.WeightMeasurementEntity

data class ProductIdWithMeasurementsIds(
    val productId: Long,
    val measurements: List<MeasurementWithRank>
)

data class MeasurementWithRank(
    val measurementId: Long,
    val rank: Float
) {
    companion object {
        const val FIRST_RANK = WeightMeasurementEntity.FIRST_RANK
    }
}
