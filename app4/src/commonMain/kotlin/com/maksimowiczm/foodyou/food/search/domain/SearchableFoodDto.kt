package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.NutritionFacts

data class SearchableFoodDto(
    val identity: FoodProductIdentity,
    val name: FoodName,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: Quantity?,
    val packageQuantity: Quantity?,
)
