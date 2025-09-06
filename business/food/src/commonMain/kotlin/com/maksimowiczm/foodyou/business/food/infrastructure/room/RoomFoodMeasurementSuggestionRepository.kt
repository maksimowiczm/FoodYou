package com.maksimowiczm.foodyou.business.food.infrastructure.room

import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.MeasurementSuggestionDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.MeasurementSuggestionEntity
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.core.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.core.shared.measurement.Measurement
import com.maksimowiczm.foodyou.core.shared.measurement.from
import com.maksimowiczm.foodyou.core.shared.measurement.rawValue
import com.maksimowiczm.foodyou.core.shared.measurement.type
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalTime::class)
internal class RoomFoodMeasurementSuggestionRepository(
    private val measurementSuggestionDao: MeasurementSuggestionDao
) : FoodMeasurementSuggestionRepository {
    override suspend fun insert(foodId: FoodId, measurement: Measurement) {
        measurementSuggestionDao.insert(measurement.toEntity(foodId))
    }

    override fun observeByFoodId(foodId: FoodId, limit: Int): Flow<List<Measurement>> =
        measurementSuggestionDao.observeByFoodId(foodId, limit).map { list ->
            list.map(MeasurementSuggestionEntity::toMeasurement)
        }
}

private fun MeasurementSuggestionDao.observeByFoodId(
    foodId: FoodId,
    limit: Int,
): Flow<List<MeasurementSuggestionEntity>> =
    this.observeByFoodId(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
        limit = limit,
    )

@OptIn(ExperimentalTime::class)
private fun Measurement.toEntity(foodId: FoodId, now: Instant = Clock.System.now()) =
    MeasurementSuggestionEntity(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
        epochSeconds = now.epochSeconds,
        type = type,
        value = rawValue,
    )

@OptIn(ExperimentalTime::class)
private fun MeasurementSuggestionEntity.toMeasurement(): Measurement =
    Measurement.from(type = type, rawValue = value)
