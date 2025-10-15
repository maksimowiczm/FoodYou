package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity

class FoodProductDto(
    val identity: FoodProductIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val barcode: Barcode?,
    val note: FoodNote?,
    val image: FoodImage?,
    val source: FoodSource?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
)
