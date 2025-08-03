package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.domain.FoodEventMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface CreateMeasurementUseCase {
    suspend fun createMeasurement(
        measurement: Measurement,
        foodId: FoodId,
        mealId: Long,
        date: LocalDate
    )
}

@OptIn(ExperimentalTime::class)
internal class CreateMeasurementUseCaseImpl(
    foodDatabase: FoodDatabase,
    foodDiaryDatabase: FoodDiaryDatabase,
    private val foodEventMapper: FoodEventMapper
) : CreateMeasurementUseCase {
    private val measurementDao = foodDiaryDatabase.measurementDao
    private val foodEventDao = foodDatabase.foodEventDao

    override suspend fun createMeasurement(
        measurement: Measurement,
        foodId: FoodId,
        mealId: Long,
        date: LocalDate
    ) {
        val entity = MeasurementEntity(
            mealId = mealId,
            epochDay = date.toEpochDays(),
            productId = (foodId as? FoodId.Product)?.id,
            recipeId = (foodId as? FoodId.Recipe)?.id,
            measurement = measurement.type,
            quantity = measurement.rawValue,
            createdAt = Clock.System.now().epochSeconds
        )

        val event = foodEventMapper.toEntity(
            model = FoodEvent.Measured(date = LocalDateTime.now(), measurement = measurement),
            foodId = foodId
        )

        measurementDao.insertMeasurement(entity)
        foodEventDao.insert(event)
    }
}
