package com.maksimowiczm.foodyou.core.domain.repository

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductWithMeasurement as ProductWithMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.RecipeWithMeasurement
import com.maksimowiczm.foodyou.core.domain.source.ProductMeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.RecipeMeasurementLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface MeasurementRepository {
    fun observeMeasurements(date: LocalDate, mealId: Long): Flow<List<FoodWithMeasurement>>

    fun observeMeasurement(measurementId: MeasurementId): Flow<FoodWithMeasurement?>

    /**
     * Get suggestions for the measurement depending on previous measurements. If there are no
     * previous measurements then return default suggestions.
     */
    fun observeSuggestions(foodId: FoodId): Flow<List<Measurement>>

    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    )

    suspend fun updateMeasurement(
        measurementId: MeasurementId,
        date: LocalDate,
        mealId: Long,
        measurement: Measurement
    )

    suspend fun removeMeasurement(measurementId: MeasurementId)
}

internal class MeasurementRepositoryImpl(
    private val recipeMeasurementDao: RecipeMeasurementLocalDataSource,
    private val productMeasurementDao: ProductMeasurementLocalDataSource,
    private val recipeRepository: RecipeRepository,
    private val measurementMapper: MeasurementMapper = MeasurementMapper
) : MeasurementRepository {

    override fun observeMeasurements(
        date: LocalDate,
        mealId: Long
    ): Flow<List<FoodWithMeasurement>> = combine(
        observeRecipeMeasurements(date, mealId),
        observeProductMeasurements(date, mealId)
    ) { recipeMeasurements, productMeasurements ->
        (recipeMeasurements + productMeasurements).sortedBy {
            it.food.name.lowercase() + (it.food.brand?.lowercase() ?: "")
        }
    }

    private fun observeProductMeasurements(date: LocalDate, mealId: Long) =
        productMeasurementDao.observeProductMeasurements(
            epochDay = date.toEpochDays(),
            mealId = mealId
        ).mapValues { it.toProductWithMeasurement() }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeRecipeMeasurements(date: LocalDate, mealId: Long) =
        recipeMeasurementDao.observeMeasurements(
            epochDay = date.toEpochDays(),
            mealId = mealId
        ).flatMapLatest { measurements ->
            if (measurements.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val flows = measurements.map { measurementEntity ->
                val recipeId = FoodId.Recipe(measurementEntity.recipeId)
                recipeRepository.observeRecipe(recipeId).filterNotNull().map { recipe ->
                    RecipeWithMeasurement(
                        measurementId = MeasurementId.Recipe(measurementEntity.id),
                        measurement = measurementMapper.toMeasurement(measurementEntity),
                        measurementDate = Instant
                            .fromEpochSeconds(measurementEntity.createdAt)
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                        mealId = measurementEntity.mealId,
                        recipe = recipe
                    )
                }
            }

            combine(flows) { it.toList() }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeMeasurement(measurementId: MeasurementId): Flow<FoodWithMeasurement?> =
        when (measurementId) {
            is MeasurementId.Product ->
                productMeasurementDao
                    .observeProductMeasurement(measurementId.id)
                    .map { it?.toProductWithMeasurement() }

            is MeasurementId.Recipe ->
                recipeMeasurementDao
                    .observeMeasurement(measurementId.id)
                    .flatMapLatest { measurementEntity ->
                        val recipeId = FoodId.Recipe(measurementEntity.recipeId)
                        recipeRepository.observeRecipe(recipeId).filterNotNull().map { recipe ->
                            RecipeWithMeasurement(
                                measurementId = measurementId,
                                measurement = measurementMapper.toMeasurement(measurementEntity),
                                measurementDate = Instant
                                    .fromEpochSeconds(measurementEntity.createdAt)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                                mealId = measurementEntity.mealId,
                                recipe = recipe
                            )
                        }
                    }
        }

    override fun observeSuggestions(foodId: FoodId): Flow<List<Measurement>> = when (foodId) {
        is FoodId.Product ->
            productMeasurementDao
                .observeProductMeasurementSuggestions(foodId.id)
                .mapValues { measurementMapper.toMeasurement(it) }

        is FoodId.Recipe ->
            recipeMeasurementDao
                .observeRecipeMeasurementSuggestions(foodId.id)
                .mapValues { measurementMapper.toMeasurement(it) }
    }

    override suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    ) {
        val type = measurementMapper.toEntity(measurement)

        val quantity = when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> measurement.quantity
            is Measurement.Serving -> measurement.quantity
        }

        val now = Clock.System.now().epochSeconds

        when (foodId) {
            is FoodId.Product -> {
                val entity = ProductMeasurementEntity(
                    mealId = mealId,
                    diaryEpochDay = date.toEpochDays(),
                    productId = foodId.id,
                    measurement = type,
                    quantity = quantity,
                    createdAt = now
                )

                productMeasurementDao.addProductMeasurement(entity)
            }

            is FoodId.Recipe -> {
                val entity = RecipeMeasurementEntity(
                    mealId = mealId,
                    epochDay = date.toEpochDays(),
                    recipeId = foodId.id,
                    measurement = type,
                    quantity = quantity,
                    createdAt = now
                )

                recipeMeasurementDao.addRecipeMeasurement(entity)
            }
        }
    }

    override suspend fun updateMeasurement(
        measurementId: MeasurementId,
        date: LocalDate,
        mealId: Long,
        measurement: Measurement
    ) {
        val type = measurementMapper.toEntity(measurement)

        val quantity = when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> measurement.quantity
            is Measurement.Serving -> measurement.quantity
        }

        when (measurementId) {
            is MeasurementId.Product -> {
                val entity = productMeasurementDao
                    .getProductMeasurement(measurementId.id)
                    ?.copy(
                        measurement = type,
                        quantity = quantity,
                        mealId = mealId,
                        diaryEpochDay = date.toEpochDays()
                    )

                if (entity == null) {
                    Logger.w(TAG) { "Attempted to update a measurement that does not exist" }
                    return
                }

                productMeasurementDao.updateProductMeasurement(entity)
            }

            is MeasurementId.Recipe -> {
                val entity = recipeMeasurementDao
                    .getRecipeMeasurement(measurementId.id)
                    ?.copy(
                        measurement = type,
                        quantity = quantity,
                        mealId = mealId,
                        epochDay = date.toEpochDays()
                    )

                if (entity == null) {
                    Logger.w(TAG) { "Attempted to update a measurement that does not exist" }
                    return
                }

                recipeMeasurementDao.updateRecipeMeasurement(entity)
            }
        }
    }

    override suspend fun removeMeasurement(measurementId: MeasurementId) {
        when (measurementId) {
            is MeasurementId.Product -> {
                val entity = productMeasurementDao.getProductMeasurement(measurementId.id) ?: return
                productMeasurementDao.deleteProductMeasurement(entity.id)
            }

            is MeasurementId.Recipe -> {
                val entity = recipeMeasurementDao.getRecipeMeasurement(measurementId.id) ?: return
                recipeMeasurementDao.deleteRecipeMeasurement(entity.id)
            }
        }
    }

    private companion object {
        const val TAG = "MeasurementRepositoryImpl"
    }

    private fun ProductWithMeasurementEntity.toProductWithMeasurement(): ProductWithMeasurement {
        val date = Instant
            .fromEpochSeconds(measurement.createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return ProductWithMeasurement(
            measurementId = MeasurementId.Product(measurement.id),
            measurement = measurementMapper.toMeasurement(measurement),
            measurementDate = date,
            mealId = measurement.mealId,
            product = with(ProductMapper) { product.toModel() }
        )
    }
}
