package com.maksimowiczm.foodyou.core.feature.addfood.data.model

data class ProductIdWithMeasurementsIds(
    val productId: Long,
    val measurements: List<Long>
)
