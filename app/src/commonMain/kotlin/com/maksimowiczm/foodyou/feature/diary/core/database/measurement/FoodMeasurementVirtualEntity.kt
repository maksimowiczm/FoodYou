package com.maksimowiczm.foodyou.feature.diary.core.database.measurement

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.diary.core.database.core.Nutrients

data class FoodMeasurementVirtualEntity(
    val productId: Long,

    // Product data
    val name: String,
    val brand: String?,
    @Embedded
    val nutrients: Nutrients,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement data
    val measurementId: Long,
    val measurement: Measurement,
    val quantity: Float
)
