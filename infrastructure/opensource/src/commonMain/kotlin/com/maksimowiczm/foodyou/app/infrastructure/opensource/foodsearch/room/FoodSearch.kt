package com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.room

import androidx.room.Embedded
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.Minerals
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.Nutrients
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.Vitamins
import com.maksimowiczm.foodyou.shared.domain.measurement.MeasurementType

data class FoodSearch(
    val productId: Long?,
    val recipeId: Long?,
    val headline: String,
    val isLiquid: Boolean,
    @Embedded val nutrients: Nutrients?,
    @Embedded val vitamins: Vitamins?,
    @Embedded val minerals: Minerals?,
    val totalWeight: Double?,
    val servingWeight: Double?,
    val measurementType: MeasurementType?,
    val measurementValue: Float?,
)
