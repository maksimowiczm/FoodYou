package com.maksimowiczm.foodyou.business.food.domain.remote

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
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
