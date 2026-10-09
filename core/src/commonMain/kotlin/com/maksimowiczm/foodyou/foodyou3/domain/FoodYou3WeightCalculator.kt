package com.maksimowiczm.foodyou.foodyou3.domain

object FoodYou3WeightCalculator {
    fun calculateWeight(
        measurement: FoodYou3Measurement,
        totalWeight: Double?,
        servingWeight: Double?,
    ): Double? =
        when (measurement) {
            is FoodYou3Measurement.ImmutableMeasurement -> measurement.metric
            is FoodYou3Measurement.Package -> totalWeight?.times(measurement.quantity)
            is FoodYou3Measurement.Serving -> servingWeight?.times(measurement.quantity)
        }
}
