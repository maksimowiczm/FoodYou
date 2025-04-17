package com.maksimowiczm.foodyou.core.domain.model.openfoodfacts

interface OpenFoodFactsProduct {
    val productName: String?
    val brands: String?
    val code: String?
    val nutrients: OpenFoodFactsNutrients?
    val packageQuantity: Float?
    val packageQuantityUnit: String?
    val servingQuantity: Float?
    val servingQuantityUnit: String?
}
