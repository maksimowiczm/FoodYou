package com.maksimowiczm.foodyou.business.shared.domain.food

import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

interface Weighted {
    val totalWeight: Double?
    val servingWeight: Double?

    fun weight(measurement: Measurement): Double? =
        when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Milliliter -> measurement.value
            is Measurement.Package -> totalWeight?.times(measurement.quantity)
            is Measurement.Serving -> servingWeight?.times(measurement.quantity)
        }
}
