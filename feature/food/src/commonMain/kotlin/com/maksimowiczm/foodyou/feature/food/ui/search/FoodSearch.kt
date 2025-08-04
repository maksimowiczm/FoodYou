package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

@Immutable
internal sealed interface FoodSearch {
    val id: FoodId
    val headline: String
    val isLiquid: Boolean

    val defaultMeasurement: Measurement

    @Immutable
    data class Product(
        override val id: FoodId.Product,
        override val headline: String,
        override val isLiquid: Boolean,
        val nutritionFacts: NutritionFacts,
        val totalWeight: Float?,
        val servingWeight: Float?,
        override val defaultMeasurement: Measurement
    ) : FoodSearch

    @Immutable
    data class Recipe(
        override val id: FoodId.Recipe,
        override val headline: String,
        override val isLiquid: Boolean,
        override val defaultMeasurement: Measurement
    ) : FoodSearch
}

internal fun Measurement.weight(food: FoodSearch.Product): Float? = when (this) {
    is Measurement.Gram -> value
    is Measurement.Milliliter -> value
    is Measurement.Package -> food.totalWeight?.let(::weight)
    is Measurement.Serving -> food.servingWeight?.let(::weight)
}
