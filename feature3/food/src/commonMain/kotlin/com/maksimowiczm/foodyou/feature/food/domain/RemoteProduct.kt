package com.maksimowiczm.foodyou.feature.food.domain

import kotlinx.serialization.Serializable

@Serializable
data class RemoteProduct(
    val name: String?,
    val brand: String?,
    val barcode: String?,
    val nutritionFacts: RemoteNutritionFacts?,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val source: FoodSource
)
