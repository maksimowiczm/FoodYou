package com.maksimowiczm.foodyou.business.shared.infrastructure.room.food

import androidx.room.Embedded
import com.maksimowiczm.foodyou.business.shared.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.Minerals
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.Nutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.Vitamins

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
