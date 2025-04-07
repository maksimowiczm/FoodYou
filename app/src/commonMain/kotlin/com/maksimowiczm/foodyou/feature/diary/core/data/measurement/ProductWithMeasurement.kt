package com.maksimowiczm.foodyou.feature.diary.core.data.measurement

import com.maksimowiczm.foodyou.feature.diary.core.data.food.Food
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Product

data class ProductWithMeasurement(
    override val measurementId: MeasurementId.Product,
    override val measurement: Measurement,
    val product: Product
) : FoodWithMeasurement {
    override val food: Food
        get() = product
}
