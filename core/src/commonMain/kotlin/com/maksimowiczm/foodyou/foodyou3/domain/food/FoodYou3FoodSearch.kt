package com.maksimowiczm.foodyou.foodyou3.domain.food

import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3Measurement
import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3WeightCalculator

sealed interface FoodYou3FoodSearch {
    val id: FoodYou3FoodId
    val headline: String
    val isLiquid: Boolean
    val suggestedMeasurement: FoodYou3Measurement

    data class Product(
        override val id: FoodYou3FoodId.Product,
        override val headline: String,
        override val isLiquid: Boolean,
        val nutritionFacts: NutritionFacts,
        val totalWeight: Double?,
        val servingWeight: Double?,
        override val suggestedMeasurement: FoodYou3Measurement,
    ) : FoodYou3FoodSearch {
        fun weight(measurement: FoodYou3Measurement): Double? =
            FoodYou3WeightCalculator.calculateWeight(
                measurement = measurement,
                totalWeight = totalWeight,
                servingWeight = servingWeight,
            )
    }

    data class Recipe(
        override val id: FoodYou3FoodId.Recipe,
        override val headline: String,
        override val isLiquid: Boolean,
        override val suggestedMeasurement: FoodYou3Measurement,
    ) : FoodYou3FoodSearch
}
