package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.food.domain.FoodBrand
import com.maksimowiczm.foodyou.food.domain.FoodIdentity
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.NutritionFacts

data class SearchableFoodDto(
    val identity: FoodIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val image: FoodImage?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val isLiquid: Boolean,
    val suggestedQuantity: Quantity,
)
