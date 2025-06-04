package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.measurement.MeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface MeasurementRepository {
    fun observeSuggestions(id: FoodId): Flow<List<Measurement>>

    /**
     * Adds a measurement for a specific food on a given date and meal.
     *
     * @param date The date when the measurement is taken.
     * @param mealId The ID of the meal associated with the measurement.
     * @param foodId The ID of the food for which the measurement is being added.
     * @param measurement The measurement details to be added.
     *
     * @return The ID of the newly created measurement.
     */
    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    ): Long

    suspend fun updateMeasurement(
        measurementId: Long,
        date: LocalDate,
        mealId: Long,
        measurement: Measurement
    )

    suspend fun removeMeasurement(measurementId: Long)

    fun observeMeasurement(measurementId: Long): Flow<FoodWithMeasurement?>
}

internal class MeasurementRepositoryImpl(
    private val measurementLocalDataSource: MeasurementLocalDataSource,
    private val foodRepository: FoodRepository,
    private val measurementMapper: MeasurementMapper
) : MeasurementRepository {

    override fun observeSuggestions(id: FoodId): Flow<List<Measurement>> = when (id) {
        is FoodId.Product ->
            measurementLocalDataSource
                .observeProductMeasurementSuggestions(id.id)
                .mapValues { measurementMapper.toMeasurement(it) }

        is FoodId.Recipe ->
            measurementLocalDataSource
                .observeRecipeMeasurementSuggestions(id.id)
                .mapValues { measurementMapper.toMeasurement(it) }
    }

    override suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    ): Long {
        val productId = (foodId as? FoodId.Product)?.id
        val recipeId = (foodId as? FoodId.Recipe)?.id

        val entity = MeasurementEntity(
            mealId = mealId,
            epochDay = date.toEpochDays(),
            productId = productId,
            recipeId = recipeId,
            measurement = measurementMapper.toEntity(measurement),
            quantity = measurementMapper.toQuantity(measurement),
            createdAt = Clock.System.now().epochSeconds
        )

        return measurementLocalDataSource.addMeasurement(entity)
    }

    override suspend fun updateMeasurement(
        measurementId: Long,
        date: LocalDate,
        mealId: Long,
        measurement: Measurement
    ) {
        val entity =
            measurementLocalDataSource.observeMeasurementById(measurementId).firstOrNull() ?: return

        val updatedEntity = entity.copy(
            epochDay = date.toEpochDays(),
            mealId = mealId,
            measurement = measurementMapper.toEntity(measurement),
            quantity = measurementMapper.toQuantity(measurement)
        )

        measurementLocalDataSource.updateMeasurement(updatedEntity)
    }

    override suspend fun removeMeasurement(measurementId: Long) {
        val entity =
            measurementLocalDataSource.observeMeasurementById(measurementId).firstOrNull() ?: return

        measurementLocalDataSource.deleteMeasurement(entity)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeMeasurement(measurementId: Long): Flow<FoodWithMeasurement?> =
        measurementLocalDataSource
            .observeMeasurementById(measurementId)
            .flatMapLatest { measurement ->
                measurement ?: return@flatMapLatest flowOf(null)

                val productId = measurement.productId
                val recipeId = measurement.recipeId

                val foodId = when {
                    productId != null -> FoodId.Product(productId)
                    recipeId != null -> FoodId.Recipe(recipeId)
                    else -> return@flatMapLatest flowOf(null)
                }

                val date = Instant
                    .fromEpochSeconds(measurement.createdAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault())

                val measurementModel = measurementMapper.toMeasurement(measurement)

                foodRepository.observeFood(foodId).mapIfNotNull { food ->
                    FoodWithMeasurement(
                        measurementId = measurement.id,
                        measurement = measurementModel,
                        measurementDate = date,
                        mealId = measurement.mealId,
                        food = food
                    )
                }
            }
}
