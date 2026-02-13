package com.maksimowiczm.foodyou.userfood.infrastructure.room.product

import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit

internal data class QuantityEntity(
    val type: QuantityType,
    val amount: Double,
    val unit: MeasurementUnit,
)
