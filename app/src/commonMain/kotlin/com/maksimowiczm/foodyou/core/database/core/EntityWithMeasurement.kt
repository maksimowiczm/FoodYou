package com.maksimowiczm.foodyou.core.database.core

import com.maksimowiczm.foodyou.core.database.measurement.Measurement

interface EntityWithMeasurement {
    val measurement: Measurement
    val quantity: Float
}
