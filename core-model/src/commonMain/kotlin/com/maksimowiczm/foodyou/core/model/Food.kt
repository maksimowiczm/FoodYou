package com.maksimowiczm.foodyou.core.model

sealed interface Food {
    val id: FoodId
    val headline: String
    val nutritionFacts: NutritionFacts
    val totalWeight: Float?
    val servingWeight: Float?
    val isLiquid: Boolean
}
