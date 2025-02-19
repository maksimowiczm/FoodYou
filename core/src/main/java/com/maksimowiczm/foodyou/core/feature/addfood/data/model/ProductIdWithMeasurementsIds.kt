package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductWeightMeasurementJunction

data class ProductIdWithMeasurementsId(
    val productId: Long,
    val measurementId: Long?,
    val rank: Float
)

fun ProductWeightMeasurementJunction.toDomain(): ProductIdWithMeasurementsId {
    return ProductIdWithMeasurementsId(
        productId = this.productId,
        measurementId = this.measurementId,
        rank = this.rank
    )
}
