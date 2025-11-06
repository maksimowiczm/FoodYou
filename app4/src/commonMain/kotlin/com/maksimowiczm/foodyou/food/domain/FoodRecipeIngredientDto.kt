package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity

class FoodRecipeIngredientDto(
    val identity: FoodProductIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val image: FoodImage?,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val per100gNutritionFacts: NutritionFacts,
    val quantity: Quantity,
)
