package com.maksimowiczm.foodyou.core.model

import kotlinx.datetime.LocalDateTime

data class ProductWithMeasurement(
    override val measurementId: MeasurementId.Product,
    override val measurement: Measurement,
    override val measurementDate: LocalDateTime,
    val product: Product
) : FoodWithMeasurement {
    override val food: Food
        get() = product
}
