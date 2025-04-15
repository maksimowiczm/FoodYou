package com.maksimowiczm.foodyou.core.domain.model

import kotlinx.datetime.LocalDateTime

data class RecipeWithMeasurement(
    override val measurementId: MeasurementId.Recipe,
    override val measurement: Measurement,
    override val measurementDate: LocalDateTime,
    val recipe: Recipe
) : FoodWithMeasurement {
    override val food: Food
        get() = recipe
}
