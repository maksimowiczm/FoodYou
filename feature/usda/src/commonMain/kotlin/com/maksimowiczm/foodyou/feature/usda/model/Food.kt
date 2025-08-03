package com.maksimowiczm.foodyou.feature.usda.model

interface Food {
    val fdcId: Int
    val description: String
    val brand: String?
    val barcode: String?

    val servingSize: Double?
    val servingSizeUnit: String?

    val packageWeight: String?

    val foodNutrients: List<FoodNutrient>
    fun getNutrient(nutrient: Nutrient): FoodNutrient? =
        foodNutrients.firstOrNull { it.number == nutrient.number }

    val url: String
        get() = "https://fdc.nal.usda.gov/food-details/$fdcId/nutrients"
}

interface FoodNutrient {
    val number: String
    val name: String
    val amount: Double
    val unit: String
}
