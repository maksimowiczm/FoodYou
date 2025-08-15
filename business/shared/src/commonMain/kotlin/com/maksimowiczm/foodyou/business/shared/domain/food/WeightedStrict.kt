package com.maksimowiczm.foodyou.business.shared.domain.food

import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

interface WeightedStrict : Weighted {
    override val totalWeight: Double
    override val servingWeight: Double

    /**
     * Calculates the weight of the food in grams or milliliters based on the provided measurement.
     */
    override fun weight(measurement: Measurement): Double =
        when (measurement) {
            is Measurement.ImmutableMeasurement -> measurement.metric
            is Measurement.Package -> totalWeight * measurement.quantity
            is Measurement.Serving -> servingWeight * measurement.quantity
        }
}
