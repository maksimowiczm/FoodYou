package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Quantity

class FoodProductDto(
    val identity: FoodProductIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val barcode: Barcode?,
    val note: FoodNote?,
    val source: FoodSource?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: Quantity?,
    val packageQuantity: Quantity?,
)
