package com.maksimowiczm.foodyou.feature.diary.core.data.measurement

import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import com.maksimowiczm.foodyou.feature.diary.core.data.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Nutrients
import com.maksimowiczm.foodyou.feature.diary.core.data.food.PortionWeight
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Product
import com.maksimowiczm.foodyou.feature.diary.core.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.core.database.measurement.FoodMeasurementVirtualEntity
import com.maksimowiczm.foodyou.feature.diary.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.core.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.feature.diary.core.database.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.core.database.measurement.SuggestionVirtualEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

interface MeasurementRepository {
    fun observeMeasurements(date: LocalDate, mealId: Long): Flow<List<FoodWithMeasurement>>

    /**
     * Get suggestion for the measurement depending on previous measurements. If there are no
     * previous measurements then return null.
     */
    suspend fun getSuggestion(foodId: FoodId): Measurement?

    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        measurement: Measurement
    )

    suspend fun removeMeasurement(measurementId: MeasurementId)

    suspend fun restoreMeasurement(measurementId: MeasurementId)
}

internal class MeasurementRepositoryImpl(database: DiaryDatabase) : MeasurementRepository {
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

    override suspend fun getSuggestion(foodId: FoodId): Measurement? = when (foodId) {
        is FoodId.Product -> {
            measurementDao
                .getProductMeasurementSuggestion(foodId.id)
                ?.toMeasurement()
        }
    }

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
