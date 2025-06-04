package com.maksimowiczm.foodyou.core.database.measurement

data class MeasurementSuggestion(
    override val quantity: Float,
    override val measurement: Measurement
) : WithMeasurement
