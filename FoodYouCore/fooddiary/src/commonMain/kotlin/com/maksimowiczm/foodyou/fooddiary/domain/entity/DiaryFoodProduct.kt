package com.maksimowiczm.foodyou.fooddiary.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.Weighted
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement

data class DiaryFoodProduct(
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val servingWeight: Double?,
    override val totalWeight: Double?,
    override val isLiquid: Boolean,
    val source: FoodSource,
    override val note: String?,
) : DiaryFood, Weighted {
    override fun weight(measurement: Measurement) =
        super<Weighted>.weight(measurement)
            ?: error("Weight is not defined for this DiaryFoodProduct")
}
