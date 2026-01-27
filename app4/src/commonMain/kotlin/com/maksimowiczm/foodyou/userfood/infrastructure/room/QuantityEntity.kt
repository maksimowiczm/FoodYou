package com.maksimowiczm.foodyou.userfood.infrastructure.room

internal data class QuantityEntity(
    val type: QuantityType,
    val amount: Double,
    val unit: MeasurementUnit,
)
