package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model

interface OpenFoodFactsProduct {
    val localizedNames: Map<String, String>
    val brand: String?
    val barcode: String
    val packageWeight: Float?
    val packageQuantityUnit: String?
    val servingWeight: Float?
    val servingQuantityUnit: String?
    val nutritionFacts: OpenFoodFactsNutrients?
    val url: String?
}
