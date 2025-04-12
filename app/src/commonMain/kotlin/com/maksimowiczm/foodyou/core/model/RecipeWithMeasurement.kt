package com.maksimowiczm.foodyou.core.model

data class RecipeWithMeasurement(
    override val measurementId: MeasurementId.Recipe,
    override val measurement: Measurement,
    val recipe: Recipe
) : FoodWithMeasurement {
    override val food: Food
        get() = recipe
}
