package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.WeightCalculator
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement

sealed interface Food {
    val id: FoodId
    val headline: String
    val totalWeight: Double?
    val servingWeight: Double?
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean

    fun weight(measurement: Measurement): Double? =
        WeightCalculator.calculateWeight(
            measurement = measurement,
            totalWeight = totalWeight,
            servingWeight = servingWeight,
        )
}
