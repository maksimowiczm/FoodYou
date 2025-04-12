package com.maksimowiczm.foodyou.core.repository

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.core.database.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.ProductMeasurementVirtualEntity
import com.maksimowiczm.foodyou.core.database.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.SuggestionVirtualEntity
import com.maksimowiczm.foodyou.core.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.core.model.RecipeIngredient
import com.maksimowiczm.foodyou.core.model.RecipeWithMeasurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    suspend fun getSuggestions(foodId: FoodId): List<Measurement>

    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    )

    suspend fun updateMeasurement(measurementId: MeasurementId, measurement: Measurement)

    suspend fun removeMeasurement(measurementId: MeasurementId)

    suspend fun restoreMeasurement(measurementId: MeasurementId)
}

internal class MeasurementRepositoryImpl(database: FoodYouDatabase) : MeasurementRepository {
    private val measurementDao: MeasurementDao = database.measurementDao

    override fun observeMeasurements(
        date: LocalDate,
        mealId: Long
    ): Flow<List<FoodWithMeasurement>> = combine(
        observeRecipeMeasurements(date, mealId),
        observeProductMeasurements(date, mealId)
    ) { recipeMeasurements, productMeasurements ->
        (recipeMeasurements + productMeasurements).sortedBy { it.measurementDate }
    }

    private fun observeProductMeasurements(date: LocalDate, mealId: Long) =
        measurementDao.observeProductMeasurements(
            epochDay = date.toEpochDays(),
            mealId = mealId
        ).map { it.map { it.toProductWithMeasurement() } }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeRecipeMeasurements(date: LocalDate, mealId: Long) =
        measurementDao.observeRecipeMeasurements(
            epochDay = date.toEpochDays(),
            mealId = mealId
        ).map { list ->
            list.map { rm ->
                val ingredients = rm.ingredients.map {
                    val product = with(ProductMapper) { it.toModel() }
                    val quantity = it.recipeIngredientEntity.quantity

                    val measurement = when (it.recipeIngredientEntity.measurement) {
                        MeasurementEntity.Gram -> Measurement.Gram(quantity)
                        MeasurementEntity.Package -> Measurement.Package(quantity)
                        MeasurementEntity.Serving -> Measurement.Serving(quantity)
                    }

                    RecipeIngredient(
                        product = product,
                        measurement = measurement
                    )
                }

                val recipe = Recipe(
                    id = FoodId.Recipe(rm.recipe.id),
                    name = rm.recipe.name,
                    servings = rm.recipe.servings,
                    ingredients = ingredients
                )

                val quantity = rm.measurement.quantity
                val measurement = when (rm.measurement.measurement) {
                    MeasurementEntity.Gram -> Measurement.Gram(quantity)
                    MeasurementEntity.Package -> Measurement.Package(quantity)
                    MeasurementEntity.Serving -> Measurement.Serving(quantity)
                }
                val date = Instant
                    .fromEpochSeconds(rm.measurement.createdAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault())

                RecipeWithMeasurement(
                    recipe = recipe,
                    measurementId = MeasurementId.Recipe(rm.measurement.id),
                    measurement = measurement,
                    measurementDate = date
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeMeasurement(measurementId: MeasurementId): Flow<FoodWithMeasurement?> =
        when (measurementId) {
            is MeasurementId.Product ->
                measurementDao
                    .observeProductMeasurement(measurementId.id)
                    .map { it?.toProductWithMeasurement() }

            is MeasurementId.Recipe ->
                measurementDao
                    .observeRecipeMeasurement(measurementId.id)
                    .map { rm ->
                        if (rm == null) return@map null

                        val ingredients = rm.ingredients.map {
                            val product = with(ProductMapper) { it.toModel() }
                            val quantity = it.recipeIngredientEntity.quantity

                            val measurement = when (it.recipeIngredientEntity.measurement) {
                                MeasurementEntity.Gram -> Measurement.Gram(quantity)
                                MeasurementEntity.Package -> Measurement.Package(quantity)
                                MeasurementEntity.Serving -> Measurement.Serving(quantity)
                            }

                            RecipeIngredient(
                                product = product,
                                measurement = measurement
                            )
                        }

                        val recipe = Recipe(
                            id = FoodId.Recipe(rm.recipe.id),
                            name = rm.recipe.name,
                            servings = rm.recipe.servings,
                            ingredients = ingredients
                        )

                        val quantity = rm.measurement.quantity
                        val measurement = when (rm.measurement.measurement) {
                            MeasurementEntity.Gram -> Measurement.Gram(quantity)
                            MeasurementEntity.Package -> Measurement.Package(quantity)
                            MeasurementEntity.Serving -> Measurement.Serving(quantity)
                        }

                        val date = Instant
                            .fromEpochSeconds(rm.measurement.createdAt)
                            .toLocalDateTime(TimeZone.currentSystemDefault())

                        RecipeWithMeasurement(
                            recipe = recipe,
                            measurementId = MeasurementId.Recipe(rm.measurement.id),
                            measurement = measurement,
                            measurementDate = date
                        )
                    }
        }

    override suspend fun getSuggestions(foodId: FoodId): List<Measurement> = when (foodId) {
        is FoodId.Product -> measurementDao.getProductMeasurementSuggestions(foodId.id)
        is FoodId.Recipe -> measurementDao.getRecipeMeasurementSuggestions(foodId.id)
    }.map { it.toMeasurement() }

    override suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    ) {
        val type = when (measurement) {
            is Measurement.Gram -> MeasurementEntity.Gram
            is Measurement.Package -> MeasurementEntity.Package
            is Measurement.Serving -> MeasurementEntity.Serving
        }

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

                measurementDao.addProductMeasurement(entity)
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

                measurementDao.addRecipeMeasurement(entity)
            }
        }
    }

    override suspend fun updateMeasurement(measurementId: MeasurementId, measurement: Measurement) {
        val type = when (measurement) {
            is Measurement.Gram -> MeasurementEntity.Gram
            is Measurement.Package -> MeasurementEntity.Package
            is Measurement.Serving -> MeasurementEntity.Serving
        }

        val quantity = when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> measurement.quantity
            is Measurement.Serving -> measurement.quantity
        }

        when (measurementId) {
            is MeasurementId.Product -> {
                val entity = measurementDao
                    .getProductMeasurement(measurementId.id)
                    ?.copy(
                        measurement = type,
                        quantity = quantity
                    )

                if (entity == null) {
                    Logger.w(TAG) { "Attempted to update a measurement that does not exist" }
                    return
                }

                measurementDao.updateProductMeasurement(entity)
            }

            is MeasurementId.Recipe -> {
                val entity = measurementDao
                    .getRecipeMeasurement(measurementId.id)
                    ?.copy(
                        measurement = type,
                        quantity = quantity
                    )

                if (entity == null) {
                    Logger.w(TAG) { "Attempted to update a measurement that does not exist" }
                    return
                }

                measurementDao.updateRecipeMeasurement(entity)
            }
        }
    }

    override suspend fun removeMeasurement(measurementId: MeasurementId) {
        when (measurementId) {
            is MeasurementId.Product -> {
                val entity = measurementDao.getProductMeasurement(measurementId.id) ?: return
                measurementDao.deleteProductMeasurement(entity.id)
            }

            is MeasurementId.Recipe -> {
                val entity = measurementDao.getRecipeMeasurement(measurementId.id) ?: return
                measurementDao.deleteRecipeMeasurement(entity.id)
            }
        }
    }

    override suspend fun restoreMeasurement(measurementId: MeasurementId) {
        when (measurementId) {
            is MeasurementId.Product -> {
                measurementDao.restoreProductMeasurement(measurementId.id)
            }

            is MeasurementId.Recipe -> {
                measurementDao.restoreRecipeMeasurement(measurementId.id)
            }
        }
    }

    private companion object {
        const val TAG = "MeasurementRepositoryImpl"
    }
}

private fun ProductMeasurementVirtualEntity.toProductWithMeasurement(): ProductWithMeasurement {
    val measurement = when (measurement) {
        MeasurementEntity.Gram -> Measurement.Gram(quantity)
        MeasurementEntity.Package -> Measurement.Package(quantity)
        MeasurementEntity.Serving -> Measurement.Serving(quantity)
    }

    val date = Instant.fromEpochSeconds(createdAt).toLocalDateTime(TimeZone.currentSystemDefault())

    return ProductWithMeasurement(
        measurementId = MeasurementId.Product(measurementId),
        measurement = measurement,
        measurementDate = date,
        product = Product(
            id = FoodId.Product(productId),
            name = name,
            brand = brand,
            barcode = null,
            nutrients = Nutrients(
                calories = nutrients.calories.toNutrientValue(),
                proteins = nutrients.proteins.toNutrientValue(),
                carbohydrates = nutrients.carbohydrates.toNutrientValue(),
                sugars = nutrients.sugars.toNutrientValue(),
                fats = nutrients.fats.toNutrientValue(),
                saturatedFats = nutrients.saturatedFats.toNutrientValue(),
                salt = nutrients.salt.toNutrientValue(),
                sodium = nutrients.sodium.toNutrientValue(),
                fiber = nutrients.fiber.toNutrientValue()
            ),
            packageWeight = packageWeight?.let { PortionWeight.Package(it) },
            servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
        )
    )
}

private fun SuggestionVirtualEntity.toMeasurement(): Measurement = when (measurement) {
    MeasurementEntity.Gram -> Measurement.Gram(quantity)
    MeasurementEntity.Package -> Measurement.Package(quantity)
    MeasurementEntity.Serving -> Measurement.Serving(quantity)
}
