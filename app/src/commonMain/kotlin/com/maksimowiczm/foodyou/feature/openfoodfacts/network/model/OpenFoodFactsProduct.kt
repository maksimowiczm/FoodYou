package com.maksimowiczm.foodyou.feature.openfoodfacts.network.model

internal interface OpenFoodFactsProduct {
    val productName: String?
    val brands: String?
    val code: String?
    val imageUrl: String?
    val nutrients: OpenFoodFactsNutrients?
    val packageQuantity: Float?
    val packageQuantityUnit: String?
    val servingQuantity: Float?
    val servingQuantityUnit: String?
}
