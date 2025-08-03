package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model

interface OpenFoodFactsProduct {
    val name: String?
    val brand: String?
    val barcode: String?
    val packageWeight: Float?
    val packageQuantityUnit: String?
    val servingWeight: Float?
    val servingQuantityUnit: String?
    val nutritionFacts: OpenFoodFactsNutrients?
    val url: String?
}
