package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.FoodSource

data class RemoteProduct(
    val name: String?,
    val brand: String?,
    val barcode: String?,
    val nutritionFacts: RemoteNutritionFacts?,
    val packageWeight: Double?,
    val servingWeight: Double?,
    val source: FoodSource,
    val isLiquid: Boolean,
)
