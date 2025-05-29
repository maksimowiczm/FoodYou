package com.maksimowiczm.foodyou.core.data.model.recipe

import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity

data class IngredientSuggestion(
    @Embedded
    val productEntity: ProductEntity,
    override val measurement: Measurement,
    override val quantity: Float
) : EntityWithMeasurement
