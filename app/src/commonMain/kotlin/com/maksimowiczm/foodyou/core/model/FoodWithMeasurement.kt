package com.maksimowiczm.foodyou.core.model

sealed interface FoodWithMeasurement {
    val measurementId: MeasurementId
    val measurement: Measurement
    val food: Food

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    val weight: Float?
        get() = when (val measurement = measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> when (val food = food) {
                is Product -> food.packageWeight?.weight?.let { it * measurement.quantity }
            }

            is Measurement.Serving -> when (val food = food) {
                is Product -> food.servingWeight?.weight?.let { it * measurement.quantity }
            }
        }
}
