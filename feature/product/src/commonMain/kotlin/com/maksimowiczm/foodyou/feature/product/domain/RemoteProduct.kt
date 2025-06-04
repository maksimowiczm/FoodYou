package com.maksimowiczm.foodyou.feature.product.domain

import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteProduct(
    val name: String?,
    val brand: String?,
    val barcode: String?,
    val nutritionFacts: RemoteNutritionFacts,
    val packageWeight: Float?,
    val servingWeight: Float?
)
