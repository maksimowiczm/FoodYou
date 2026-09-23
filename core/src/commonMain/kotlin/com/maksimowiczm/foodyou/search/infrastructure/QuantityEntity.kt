package com.maksimowiczm.foodyou.search.infrastructure

import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit

data class QuantityEntity(val type: QuantityType, val amount: Double, val unit: MeasurementUnit)
