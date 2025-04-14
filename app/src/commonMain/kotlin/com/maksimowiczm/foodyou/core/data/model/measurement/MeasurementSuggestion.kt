package com.maksimowiczm.foodyou.core.data.model.measurement

import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement

data class MeasurementSuggestion(
    override val quantity: Float,
    override val measurement: Measurement
) : EntityWithMeasurement
