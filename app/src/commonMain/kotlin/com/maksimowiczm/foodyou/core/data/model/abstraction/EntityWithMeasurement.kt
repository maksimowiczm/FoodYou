package com.maksimowiczm.foodyou.core.data.model.abstraction

import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement

interface EntityWithMeasurement {
    val measurement: Measurement
    val quantity: Float
}
