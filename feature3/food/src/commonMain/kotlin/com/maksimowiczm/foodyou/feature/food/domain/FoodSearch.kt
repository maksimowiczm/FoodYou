package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

sealed interface FoodSearch {
    val id: FoodId
    val headline: String

    val defaultMeasurement: Measurement

    data class Product(
        override val id: FoodId.Product,
        override val headline: String,
        val nutritionFacts: NutritionFacts,
        val totalWeight: Float?,
        val servingWeight: Float?
    ) : FoodSearch {

        override val defaultMeasurement: Measurement = when {
            servingWeight != null -> Measurement.Serving(1f)
            totalWeight != null -> Measurement.Package(1f)
            else -> Measurement.Gram(100f)
        }
    }

    data class Recipe(override val id: FoodId.Recipe, override val headline: String) : FoodSearch {

        override val defaultMeasurement: Measurement = Measurement.Serving(1f)
    }
}
