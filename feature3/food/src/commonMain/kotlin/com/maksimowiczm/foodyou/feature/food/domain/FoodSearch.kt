package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

data class FoodSearch(
    val id: FoodId,
    val headline: String,
    val nutritionFacts: NutritionFacts,
    val totalWeight: Float?,
    val servingWeight: Float?
) {
    val defaultMeasurement: Measurement = when {
        servingWeight != null -> Measurement.Serving(1f)
        totalWeight != null -> Measurement.Package(1f)
        else -> Measurement.Gram(100f)
    }
}
