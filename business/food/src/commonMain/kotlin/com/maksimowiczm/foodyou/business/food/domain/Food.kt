package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId

sealed interface Food {
    val id: FoodId
    val headline: String
    val totalWeight: Double?
    val servingWeight: Double?
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
}
