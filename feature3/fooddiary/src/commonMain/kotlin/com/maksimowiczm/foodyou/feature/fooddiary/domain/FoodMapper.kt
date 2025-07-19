package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodWithMeasurement as FoodWithMeasurementEntity
import com.maksimowiczm.foodyou.feature.measurement.domain.from
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface FoodMapper {
    fun toFoodWithMeasurement(entity: FoodWithMeasurementEntity): FoodWithMeasurement
}

internal class FoodMapperImpl(private val productMapper: ProductMapper) : FoodMapper {

    @OptIn(ExperimentalTime::class)
    override fun toFoodWithMeasurement(entity: FoodWithMeasurementEntity): FoodWithMeasurement {
        val product = entity.product?.let(productMapper::toModel)

        val food = when {
            product != null -> product
            else -> error("FoodWithMeasurement must have either product or recipe")
        }

        val date = Instant
            .fromEpochSeconds(entity.measurement.createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return FoodWithMeasurement(
            measurementId = entity.measurement.id,
            measurement = com.maksimowiczm.foodyou.feature.measurement.domain.Measurement.from(
                entity.measurement.measurement,
                entity.measurement.quantity
            ),
            measurementDate = date,
            mealId = entity.measurement.mealId,
            food = food
        )
    }
}
