package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.Weighted

sealed interface Food : Weighted {
    val id: FoodId
    val headline: String
    override val totalWeight: Double?
    override val servingWeight: Double?
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
}
