package com.maksimowiczm.foodyou.business.shared.domain.food

import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

interface WeightedStrict : Weighted {
    override val totalWeight: Double
    override val servingWeight: Double

    override fun weight(measurement: Measurement): Double =
        when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Milliliter -> measurement.value
            is Measurement.Package -> totalWeight * measurement.quantity
            is Measurement.Serving -> servingWeight * measurement.quantity
        }
}
