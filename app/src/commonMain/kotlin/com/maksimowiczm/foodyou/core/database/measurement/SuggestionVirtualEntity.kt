package com.maksimowiczm.foodyou.core.database.measurement

import com.maksimowiczm.foodyou.core.database.core.EntityWithMeasurement

data class SuggestionVirtualEntity(
    override val quantity: Float,
    override val measurement: Measurement
) : EntityWithMeasurement
