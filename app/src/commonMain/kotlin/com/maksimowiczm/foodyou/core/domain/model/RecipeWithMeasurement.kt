package com.maksimowiczm.foodyou.core.domain.model

import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import kotlinx.datetime.LocalDateTime

data class RecipeWithMeasurement(
    override val measurementId: MeasurementId.Recipe,
    override val measurement: Measurement,
    override val measurementDate: LocalDateTime,
    override val mealId: Long,
    val recipe: Recipe
) : FoodWithMeasurement {
    override val food: Food
        get() = recipe
}
