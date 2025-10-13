package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.domain.Weight

data class SearchableFoodDto(
    val identity: FoodProductIdentity,
    val name: FoodName,
    val nutritionFacts: NutritionFacts,
    val servingWeight: Weight?,
    val totalWeight: Weight?,
)
