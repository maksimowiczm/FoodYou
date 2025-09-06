package com.maksimowiczm.foodyou.app.infrastructure.room.food

import androidx.room.Embedded
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.Minerals
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.Nutrients
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.Vitamins
import com.maksimowiczm.foodyou.shared.measurement.MeasurementType

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
