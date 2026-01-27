package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model

internal interface OpenFoodFactsProduct {
    val localizedNames: Map<String, String>
    val brand: String?
    val barcode: String
    val packageWeight: Double?
    val packageQuantityUnit: String?
    val servingWeight: Double?
    val servingQuantityUnit: String?
    val nutritionFacts: OpenFoodFactsNutrients?
    val url: String?
    val thumbnailUrl: String?
    val imageUrl: String?
}
