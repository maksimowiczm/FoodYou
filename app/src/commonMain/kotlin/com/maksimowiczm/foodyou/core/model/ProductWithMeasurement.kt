package com.maksimowiczm.foodyou.core.model

data class ProductWithMeasurement(
    override val measurementId: MeasurementId.Product,
    override val measurement: Measurement,
    val product: Product
) : FoodWithMeasurement {
    override val food: Food
        get() = product
}
