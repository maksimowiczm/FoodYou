package com.maksimowiczm.foodyou.feature.fooddiary.data

import com.maksimowiczm.foodyou.feature.measurement.data.Measurement
import com.maksimowiczm.foodyou.feature.measurement.data.WithMeasurement

data class MeasurementSuggestion(
    override val measurement: Measurement,
    override val quantity: Float
) : WithMeasurement
