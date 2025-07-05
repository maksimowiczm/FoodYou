package com.maksimowiczm.foodyou.feature.food.domain

sealed interface Food {
    val id: FoodId
    val headline: String
    val nutritionFacts: NutritionFacts
    val totalWeight: Float?
    val servingWeight: Float?
    val note: String?
}
