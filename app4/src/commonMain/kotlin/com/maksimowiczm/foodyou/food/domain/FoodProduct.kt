package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

@JvmInline value class FoodProductId(val value: String)

class FoodProduct(
    val id: FoodProductId,
    val name: FoodName,
    val brand: FoodBrand?,
    val barcode: Barcode?,
    val note: FoodNote?,
    val source: FoodSource,
    val nutritionFacts: NutritionFacts,
    val servingWeight: ServingWeight?,
    val packageWeight: PackageWeight?,
)
