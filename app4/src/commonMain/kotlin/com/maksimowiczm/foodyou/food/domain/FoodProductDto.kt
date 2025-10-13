package com.maksimowiczm.foodyou.food.domain

class FoodProductDto(
    val identity: FoodProductIdentity,
    val name: FoodName,
    val brand: FoodBrand?,
    val barcode: Barcode?,
    val note: FoodNote?,
    val source: FoodSource?,
    val nutritionFacts: NutritionFacts,
    val servingWeight: Weight?,
    val packageWeight: Weight?,
)
