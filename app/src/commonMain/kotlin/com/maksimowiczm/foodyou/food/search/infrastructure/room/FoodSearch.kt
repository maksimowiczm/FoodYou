package com.maksimowiczm.foodyou.food.search.infrastructure.room

import androidx.room.Embedded
import com.maksimowiczm.foodyou.common.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.common.infrastructure.room.Minerals
import com.maksimowiczm.foodyou.common.infrastructure.room.Nutrients
import com.maksimowiczm.foodyou.common.infrastructure.room.Vitamins

data class FoodSearch(
    val productId: Long?,
    val recipeId: Long?,
    val headline: String,
    val foodType: Int,
    val usageCount: Int,
    val lastUsedSeconds: Long,
    val nameLength: Int,
    val isLiquid: Boolean,
    @Embedded val nutrients: Nutrients?,
    @Embedded val vitamins: Vitamins?,
    @Embedded val minerals: Minerals?,
    val totalWeight: Double?,
    val servingWeight: Double?,
    val measurementType: MeasurementType?,
    val measurementValue: Double?,
)
