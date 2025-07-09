package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts

data class Food(
    val id: FoodId,
    val headline: String,
    val nutritionFacts: NutritionFacts,
    val totalWeight: Float?,
    val servingWeight: Float?
)
