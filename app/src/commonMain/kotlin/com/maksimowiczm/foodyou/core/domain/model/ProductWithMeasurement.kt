package com.maksimowiczm.foodyou.core.domain.model

import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product
import kotlinx.datetime.LocalDateTime

data class ProductWithMeasurement(
    override val measurementId: MeasurementId.Product,
    override val measurement: Measurement,
    override val measurementDate: LocalDateTime,
    override val mealId: Long,
    val product: Product
) : FoodWithMeasurement {
    override val food: Food
        get() = product
}
