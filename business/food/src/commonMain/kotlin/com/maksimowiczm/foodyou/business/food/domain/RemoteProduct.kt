package com.maksimowiczm.foodyou.business.food.domain

import kotlinx.serialization.Serializable

@Serializable
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
