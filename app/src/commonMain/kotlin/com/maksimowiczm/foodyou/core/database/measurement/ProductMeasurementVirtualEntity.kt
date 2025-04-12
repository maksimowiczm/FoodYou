package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.core.NutrientsEmbedded

data class ProductMeasurementVirtualEntity(
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
    val quantity: Float,
    /**
     * Epoch seconds
     */
    val createdAt: Long
)
