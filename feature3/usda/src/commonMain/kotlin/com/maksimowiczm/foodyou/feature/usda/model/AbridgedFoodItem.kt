package com.maksimowiczm.foodyou.feature.usda.model

interface AbridgedFoodItem {
    val description: String
    val foodNutrients: List<AbridgedFoodNutrient>
    val brand: String?
    val barcode: String?

    fun getNutrient(nutrient: Nutrient): AbridgedFoodNutrient? =
        foodNutrients.firstOrNull { it.number == nutrient.number }
}
