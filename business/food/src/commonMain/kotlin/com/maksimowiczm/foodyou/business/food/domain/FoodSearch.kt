package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.Weighted
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement

sealed interface FoodSearch {
    val id: FoodId
    val headline: String
    val isLiquid: Boolean
    val suggestedMeasurement: Measurement

    data class Product(
        override val id: FoodId.Product,
        override val headline: String,
        override val isLiquid: Boolean,
        val nutritionFacts: NutritionFacts,
        override val totalWeight: Double?,
        override val servingWeight: Double?,
        override val suggestedMeasurement: Measurement,
    ) : FoodSearch, Weighted

    data class Recipe(
        override val id: FoodId.Recipe,
        override val headline: String,
        override val isLiquid: Boolean,
        override val suggestedMeasurement: Measurement,
    ) : FoodSearch
}
