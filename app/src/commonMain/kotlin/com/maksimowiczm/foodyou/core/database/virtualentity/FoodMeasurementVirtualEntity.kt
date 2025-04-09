package com.maksimowiczm.foodyou.core.database.virtualentity

import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.embedded.NutrientsEmbedded
import com.maksimowiczm.foodyou.core.database.entity.Measurement

data class FoodMeasurementVirtualEntity(
    val productId: Long,

    // Product data
    val name: String,
    val brand: String?,
    @Embedded
    val nutrients: NutrientsEmbedded,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement data
    val measurementId: Long,
    val measurement: Measurement,
    val quantity: Float
)
