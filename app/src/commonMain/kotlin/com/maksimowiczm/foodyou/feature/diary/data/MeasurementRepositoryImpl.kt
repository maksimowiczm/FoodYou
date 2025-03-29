package com.maksimowiczm.foodyou.feature.diary.data

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementSuggestion as IMeasurementSuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.data.model.toWeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.measurement.CombinedMeasurement
import com.maksimowiczm.foodyou.feature.diary.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.feature.diary.database.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.diary.database.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.WeightMeasurementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class MeasurementRepositoryImpl(database: DiaryDatabase) : MeasurementRepository {
    val measurementDao: MeasurementDao = database.measurementDao

    override suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        foodId: FoodId,
        weightMeasurement: WeightMeasurement
    ) {
        val quantity = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        val epochSeconds = Clock.System.now().epochSeconds

        when (foodId) {
            is FoodId.Product -> {
                val entity = WeightMeasurementEntity(
                    mealId = mealId,
                    diaryEpochDay = date.toEpochDays(),
                    productId = foodId.productId,
                    measurement = weightMeasurement.asEnum(),
                    quantity = quantity,
                    createdAt = epochSeconds
                )

                measurementDao.insertWeightMeasurement(entity)
            }

            is FoodId.Recipe -> {
                val entity = RecipeMeasurementEntity(
                    mealId = mealId,
                    diaryEpochDay = date.toEpochDays(),
                    recipeId = foodId.recipeId,
                    measurement = weightMeasurement.asEnum(),
                    quantity = quantity,
                    createdAt = epochSeconds
                )

                measurementDao.insertWeightMeasurement(entity)
            }
        }
    }

    override suspend fun removeMeasurement(id: MeasurementId) {
        when (id) {
            is MeasurementId.Product -> measurementDao.removeProductWeightMeasurement(
                id.measurementId
            )

            is MeasurementId.Recipe -> measurementDao.removeRecipeWeightMeasurement(
                id.measurementId
            )
        }
    }

    override suspend fun restoreMeasurement(id: MeasurementId) {
        when (id) {
            is MeasurementId.Product -> measurementDao.restoreProductWeightMeasurement(
                id.measurementId
            )

            is MeasurementId.Recipe -> measurementDao.restoreRecipeWeightMeasurement(
                id.measurementId
            )
        }
    }

    override suspend fun updateMeasurement(
        id: MeasurementId,
        weightMeasurement: WeightMeasurement
    ) {
        val quantity = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        when (id) {
            is MeasurementId.Product -> {
                val measurementId = id.measurementId

                val entity = measurementDao.getProductWeightMeasurementById(
                    id = measurementId,
                    isDeleted = false
                )

                if (entity == null) {
                    Logger.w(TAG) { "Measurement not found for ID $measurementId." }
                    return
                }

                val updatedEntity = entity.copy(
                    measurement = weightMeasurement.asEnum(),
                    quantity = quantity
                )

                measurementDao.updateWeightMeasurement(updatedEntity)
            }

            is MeasurementId.Recipe -> {
                val measurementId = id.measurementId

                val entity = measurementDao.getRecipeWeightMeasurementById(
                    id = measurementId,
                    isDeleted = false
                )

                if (entity == null) {
                    Logger.w(TAG) { "Measurement not found for ID $measurementId." }
                    return
                }

                val updatedEntity = entity.copy(
                    measurement = weightMeasurement.asEnum(),
                    quantity = quantity
                )

                measurementDao.updateWeightMeasurement(updatedEntity)
            }
        }
    }

    override fun observeMeasurements(mealId: Long?, date: LocalDate): Flow<List<FoodMeasurement>> =
        measurementDao.observeMeasurements(
            mealId = mealId,
            epochDay = date.toEpochDays(),
            isDeleted = false
        ).map { list ->
            list.map {
                it.toFoodMeasurement()
            }
        }

    override fun observeMeasurementById(measurementId: MeasurementId): Flow<FoodMeasurement?> =
        when (measurementId) {
            is MeasurementId.Product -> measurementDao.observeProductMeasurement(
                id = measurementId.measurementId,
                isDeleted = false
            ).map { entity ->
                entity?.toFoodMeasurement()
            }

            is MeasurementId.Recipe -> measurementDao.observeRecipeMeasurement(
                id = measurementId.measurementId,
                isDeleted = false
            ).map { entity ->
                entity?.toFoodMeasurement()
            }
        }

    override fun observeMeasurementSuggestionByFood(foodId: FoodId): Flow<IMeasurementSuggestion> {
        when (foodId) {
            is FoodId.Product -> {
                return measurementDao.observeProductMeasurementsByProductId(
                    id = foodId.productId
                ).map { it.toDomain() }
            }

            is FoodId.Recipe -> TODO()
        }
    }

    companion object {
        private const val TAG = "MeasurementRepositoryImpl"
    }
}

private fun CombinedMeasurement.toFoodMeasurement(): FoodMeasurement {
    val foodId = when {
        productId != null -> FoodId.Product(productId)
        recipeId != null -> FoodId.Recipe(recipeId)
        else -> error("Invalid food ID for $this")
    }

    return FoodMeasurement(
        foodId = foodId,
        measurement = measurement.toWeightMeasurement(quantity),
        measurementId = MeasurementId.Product(measurementId)
    )
}

private fun WeightMeasurementEntity.toFoodMeasurement(): FoodMeasurement = FoodMeasurement(
    foodId = FoodId.Product(productId),
    measurement = measurement.toWeightMeasurement(quantity),
    measurementId = MeasurementId.Product(id)
)

private fun RecipeMeasurementEntity.toFoodMeasurement(): FoodMeasurement = FoodMeasurement(
    foodId = FoodId.Recipe(recipeId),
    measurement = measurement.toWeightMeasurement(quantity),
    measurementId = MeasurementId.Recipe(id)
)

private fun List<MeasurementSuggestion>.toDomain() = object : IMeasurementSuggestion {
    override val packageSuggestion: WeightMeasurement.Package?
        get() = singleOrNull { it.measurement == WeightMeasurementEnum.Package }
            ?.let { WeightMeasurement.Package(it.quantity) }

    override val servingSuggestion: WeightMeasurement.Serving?
        get() = singleOrNull { it.measurement == WeightMeasurementEnum.Serving }
            ?.let { WeightMeasurement.Serving(it.quantity) }

    override val weightSuggestion: WeightMeasurement.WeightUnit
        get() = singleOrNull { it.measurement == WeightMeasurementEnum.WeightUnit }
            ?.let { WeightMeasurement.WeightUnit(it.quantity) } ?: WeightMeasurement.WeightUnit(
            100f
        )
}
