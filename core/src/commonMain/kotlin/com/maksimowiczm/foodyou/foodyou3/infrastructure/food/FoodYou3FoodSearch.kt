package com.maksimowiczm.foodyou.foodyou3.infrastructure.food

import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3MeasurementType
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3Minerals
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3Nutrients
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3Vitamins

data class FoodYou3FoodSearch(
    val productId: Long?,
    val recipeId: Long?,
    val headline: String,
    val isLiquid: Boolean,
    val nutrients: FoodYou3Nutrients?,
    val vitamins: FoodYou3Vitamins?,
    val minerals: FoodYou3Minerals?,
    val totalWeight: Double?,
    val servingWeight: Double?,
    val measurementType: FoodYou3MeasurementType?,
    val measurementValue: Double?,
)
