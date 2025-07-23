package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.measurement.data.Measurement

sealed interface Food {
    val id: FoodId
    val headline: String
    val nutritionFacts: NutritionFacts
    val totalWeight: Float?
    val servingWeight: Float?
    val note: String?

    /**
     * Indicates whether the food is liquid or solid.
     */
    val isLiquid: Boolean
}

val Food.possibleMeasurementTypes: Set<Measurement>
    get() = Measurement.entries.filter { type ->
        when (type) {
            Measurement.Gram -> !isLiquid
            Measurement.Milliliter -> isLiquid
            Measurement.Package -> totalWeight != null
            Measurement.Serving -> servingWeight != null
        }
    }.toSet()
