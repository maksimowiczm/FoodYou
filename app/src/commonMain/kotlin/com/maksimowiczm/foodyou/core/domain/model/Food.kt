package com.maksimowiczm.foodyou.core.domain.model

sealed interface Food {
    val id: FoodId
    val name: String
    val brand: String?
    val nutritionFacts: NutritionFacts
    val packageWeight: PortionWeight.Package?
    val servingWeight: PortionWeight.Serving?

    val headline: String
        get() = if (brand.isNullOrBlank()) name else "$name ($brand)"
}
