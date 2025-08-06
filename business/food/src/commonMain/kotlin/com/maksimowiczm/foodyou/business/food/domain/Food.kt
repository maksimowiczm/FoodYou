package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

sealed interface Food {
    val id: FoodId
    val totalWeight: Double?
    val servingWeight: Double?
    val nutritionFacts: NutritionFacts
}
