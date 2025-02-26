package com.maksimowiczm.foodyou.database.entity

import androidx.room.Embedded

data class ProductSearchEntity(
    @Embedded(prefix = "p_")
    val product: ProductEntity,

    @Embedded(prefix = "m_")
    val weightMeasurement: WeightMeasurementEntity?,

    val todaysMeasurement: Boolean
)
