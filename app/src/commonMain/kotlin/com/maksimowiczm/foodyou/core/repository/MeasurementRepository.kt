package com.maksimowiczm.foodyou.core.repository

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.measurement.FoodMeasurementVirtualEntity
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.core.database.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.SuggestionVirtualEntity
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.model.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

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
    ): Flow<List<FoodWithMeasurement>> = measurementDao.observeMeasurements(
        epochDay = date.toEpochDays(),
        mealId = mealId
    ).map { list ->
        list.map { it.toFoodWithMeasurement() }
    }

    override fun observeMeasurement(measurementId: MeasurementId): Flow<FoodWithMeasurement?> =
        when (measurementId) {
            is MeasurementId.Product -> measurementDao.observeMeasurement(measurementId.id)
        }.map { it?.toFoodWithMeasurement() }

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
        }
    }

    override suspend fun removeMeasurement(measurementId: MeasurementId) {
        when (measurementId) {
            is MeasurementId.Product -> {
                val entity = measurementDao.getProductMeasurement(measurementId.id) ?: return
                measurementDao.deleteProductMeasurement(entity.id)
            }
        }
    }

    override suspend fun restoreMeasurement(measurementId: MeasurementId) {
        when (measurementId) {
            is MeasurementId.Product -> {
                measurementDao.restoreProductMeasurement(measurementId.id)
            }
        }
    }

    private companion object {
        const val TAG = "MeasurementRepositoryImpl"
    }
}

private fun FoodMeasurementVirtualEntity.toFoodWithMeasurement(): FoodWithMeasurement {
    val measurement = when (measurement) {
        MeasurementEntity.Gram -> Measurement.Gram(quantity)
        MeasurementEntity.Package -> Measurement.Package(quantity)
        MeasurementEntity.Serving -> Measurement.Serving(quantity)
    }

    return ProductWithMeasurement(
        measurementId = MeasurementId.Product(measurementId),
        measurement = measurement,
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
