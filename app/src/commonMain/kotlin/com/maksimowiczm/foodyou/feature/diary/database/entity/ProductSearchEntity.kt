package com.maksimowiczm.foodyou.feature.diary.database.entity

import androidx.room.Embedded

data class ProductSearchEntity(
    @Embedded(prefix = "p_")
    val product: ProductEntity,

    @Embedded(prefix = "m_")
    val weightMeasurement: WeightMeasurementEntity?,

    val todaysMeasurement: Boolean
)
