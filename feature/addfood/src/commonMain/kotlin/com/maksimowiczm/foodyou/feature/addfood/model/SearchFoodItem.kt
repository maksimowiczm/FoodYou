package com.maksimowiczm.foodyou.feature.addfood.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Measurement.Gram
import com.maksimowiczm.foodyou.core.model.Measurement.Serving

@Immutable
internal data class SearchFoodItem(
    val food: Food,
    val measurement: Measurement,
    val measurementId: Long?,
    val uniqueId: String
) {
    val headline: String
        get() = food.headline

    val proteins: Float
        get() = food.nutritionFacts.proteins.value

    val carbohydrates: Float
        get() = food.nutritionFacts.carbohydrates.value

    val fats: Float
        get() = food.nutritionFacts.fats.value

    val calories: Float
        get() = food.nutritionFacts.calories.value

    val isSelected: Boolean
        get() = measurementId != null

    val weight: Float?
        get() = with(measurement) {
            when (this) {
                is Gram -> value
                is Measurement.Package -> food.totalWeight?.let { weight(it) }
                is Serving -> food.servingWeight?.let { weight(it) }
            }
        }
}
