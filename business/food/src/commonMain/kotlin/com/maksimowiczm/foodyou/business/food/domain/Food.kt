package com.maksimowiczm.foodyou.business.food.domain

sealed interface Food {
    val id: FoodId
    val totalWeight: Double?
    val servingWeight: Double?
    val nutritionFacts: NutritionFacts
}
