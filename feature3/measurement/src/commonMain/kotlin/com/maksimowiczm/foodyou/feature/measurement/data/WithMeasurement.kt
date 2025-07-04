package com.maksimowiczm.foodyou.feature.measurement.data

interface WithMeasurement {
    /**
     * Measurement type.
     */
    val measurement: Measurement

    /**
     * The quantity of the measurement.
     */
    val quantity: Float
}
