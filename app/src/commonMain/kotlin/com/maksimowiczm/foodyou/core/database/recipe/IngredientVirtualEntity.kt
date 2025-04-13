package com.maksimowiczm.foodyou.core.database.recipe

import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.measurement.Measurement
import com.maksimowiczm.foodyou.core.database.product.ProductEntity

data class IngredientVirtualEntity(
    @Embedded
    val productEntity: ProductEntity,
    val measurement: Measurement,
    val quantity: Float
)
