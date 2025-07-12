package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.Food as DetailedFood
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

data class Food(
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

val DetailedFood.defaultMeasurement: Measurement
    get() = when {
        servingWeight != null -> Measurement.Serving(1f)
        totalWeight != null -> Measurement.Package(1f)
        else -> Measurement.Gram(100f)
    }
