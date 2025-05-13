package com.maksimowiczm.foodyou.feature.product.domain

data class RemoteProduct(
    val name: String?,
    val brand: String?,
    val barcode: String?,
    val nutritionFacts: RemoteNutritionFacts,
    val packageWeight: Float?,
    val servingWeight: Float?
)
