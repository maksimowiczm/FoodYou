package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.food.Weighted
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

sealed interface FoodSearch {
    val id: FoodId
    val headline: String
    val isLiquid: Boolean
    val defaultMeasurement: Measurement

    data class Product(
        override val id: FoodId.Product,
        override val headline: String,
        override val isLiquid: Boolean,
        val nutritionFacts: NutritionFacts,
        override val totalWeight: Double?,
        override val servingWeight: Double?,
        override val defaultMeasurement: Measurement,
    ) : FoodSearch, Weighted

    data class Recipe(
        override val id: FoodId.Recipe,
        override val headline: String,
        override val isLiquid: Boolean,
        override val defaultMeasurement: Measurement,
    ) : FoodSearch
}
