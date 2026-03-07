package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model

internal interface Food {
    val fdcId: Int
    val description: String
    val brandOwner: String?
    val brandName: String?
    val gtinUpc: String?

    val servingSize: Double?
    val servingSizeUnit: String?

    val packageWeight: String?

    val foodNutrients: List<FoodNutrient>

    fun getNutrient(nutrient: Nutrient): FoodNutrient? =
        foodNutrients.firstOrNull { it.number == nutrient.number }

    val url: String
        get() = "https://fdc.nal.usda.gov/food-details/$fdcId/nutrients"
}

internal interface FoodNutrient {
    val number: String
    val name: String
    val amount: Double?
    val unit: String
}
