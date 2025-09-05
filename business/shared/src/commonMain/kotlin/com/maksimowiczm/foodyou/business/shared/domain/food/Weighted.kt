package com.maksimowiczm.foodyou.business.shared.domain.food

import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement

interface Weighted {
    val totalWeight: Double?
    val servingWeight: Double?

    /**
     * Calculates the weight of the food in grams or milliliters based on the provided measurement.
     */
    fun weight(measurement: Measurement): Double? =
        when (measurement) {
            is Measurement.ImmutableMeasurement -> measurement.metric
            is Measurement.Package -> totalWeight?.times(measurement.quantity)
            is Measurement.Serving -> servingWeight?.times(measurement.quantity)
        }
}
