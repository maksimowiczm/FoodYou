package com.maksimowiczm.foodyou.core.domain

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEnum
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import kotlin.collections.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

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

    suspend fun unpackRecipe(
        date: LocalDate,
        mealId: Long,
        recipeId: FoodId.Recipe,
        measurement: Measurement
    )

    suspend fun removeMeasurement(measurementId: Long)

    fun observeMeasurement(measurementId: Long): Flow<FoodWithMeasurement?>

    fun observeMeasurements(mealId: Long, date: LocalDate): Flow<List<FoodWithMeasurement>>
}

internal class MeasurementRepositoryImpl(
    private val measurementLocalDataSource: MeasurementLocalDataSource,
    private val foodRepository: FoodRepository,
    private val measurementMapper: MeasurementMapper
) : MeasurementRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSuggestions(id: FoodId): Flow<List<Measurement>> {
        val flow = foodRepository
            .observeFood(id)
            .filterNotNull()
            .flatMapLatest { food ->
                val productId = (food.id as? FoodId.Product)?.id
                val recipeId = (food.id as? FoodId.Recipe)?.id

                val packageSuggestion = measurementLocalDataSource.observeAllMeasurementsByType(
                    productId = productId,
                    recipeId = recipeId,
                    measurement = MeasurementEnum.Package
                ).map {
                    it.firstOrNull() ?: MeasurementSuggestion(1f, MeasurementEnum.Package)
                }.map {
                    it.takeIf { food.totalWeight != null }
                }

                val servingSuggestion = measurementLocalDataSource.observeAllMeasurementsByType(
                    productId = productId,
                    recipeId = recipeId,
                    measurement = MeasurementEnum.Serving
                ).map {
                    it.firstOrNull() ?: MeasurementSuggestion(1f, MeasurementEnum.Serving)
                }.map {
                    it.takeIf { food.servingWeight != null }
                }

                val raw = if (food.isLiquid) {
                    measurementLocalDataSource.observeAllMeasurementsByType(
                        productId = productId,
                        recipeId = recipeId,
                        measurement = MeasurementEnum.Milliliter
                    ).map {
                        it.firstOrNull() ?: MeasurementSuggestion(100f, MeasurementEnum.Milliliter)
                    }
                } else {
                    measurementLocalDataSource.observeAllMeasurementsByType(
                        productId = productId,
                        recipeId = recipeId,
                        measurement = MeasurementEnum.Gram
                    ).map {
                        it.firstOrNull() ?: MeasurementSuggestion(100f, MeasurementEnum.Gram)
                    }
                }

                combine(
                    packageSuggestion,
                    servingSuggestion,
                    raw
                ) { packageSuggestion, servingSuggestion, raw ->
                    listOfNotNull(
                        packageSuggestion,
                        servingSuggestion,
                        raw
                    )
                }
            }

        return flow.mapValues { measurementMapper.toMeasurement(it) }
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

    override suspend fun unpackRecipe(
        date: LocalDate,
        mealId: Long,
        recipeId: FoodId.Recipe,
        measurement: Measurement
    ) {
        val recipe = foodRepository.observeFood(recipeId).firstOrNull()

        if (recipe == null) {
            Logger.w(TAG) { "Recipe with id ${recipeId.id} not found for exploding measurements." }
            return
        }

        if (recipe !is Recipe) {
            Logger.w(TAG) { "Food is not a recipe but a ${recipe.javaClass.simpleName}" }
            return
        }

        val measuredIngredients = recipe.measuredIngredients(measurement)

        measuredIngredients.forEach { (ingredient, measurement) ->
            if (measurement == null) {
                Logger.w(TAG) { "Ingredient ${ingredient.headline} has no measurement, skipping." }
                return@forEach
            }

            addMeasurement(
                date = date,
                mealId = mealId,
                foodId = ingredient.id,
                measurement = measurement
            )
        }
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

                val date = LocalDate.fromEpochDays(measurement.epochDay)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeMeasurements(
        mealId: Long,
        date: LocalDate
    ): Flow<List<FoodWithMeasurement>> {
        return measurementLocalDataSource.observeMeasurements(
            mealId = mealId,
            epochDay = date.toEpochDays()
        ).flatMapLatest { measurements ->
            if (measurements.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val result = measurements.map { measurement ->
                foodRepository.observeFood(measurement.foodId).filterNotNull().map { food ->
                    FoodWithMeasurement(
                        measurementId = measurement.id,
                        measurement = measurementMapper.toMeasurement(measurement),
                        measurementDate = LocalDate.fromEpochDays(measurement.epochDay),
                        mealId = measurement.mealId,
                        food = food
                    )
                }
            }.combine { it.toList() }.map {
                it.sortedBy {
                    it.food.headline.lowercase()
                }
            }

            result
        }
    }

    private companion object {
        const val TAG = "MeasurementRepository"
    }
}

private val MeasurementEntity.foodId: FoodId
    get() {
        val productId = this.productId
        val recipeId = this.recipeId

        return when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Measurement must have either productId or recipeId")
        }
    }
