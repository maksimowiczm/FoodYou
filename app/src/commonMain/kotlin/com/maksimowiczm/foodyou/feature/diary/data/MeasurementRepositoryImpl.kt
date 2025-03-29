package com.maksimowiczm.foodyou.feature.diary.data

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.WeightMeasurementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class MeasurementRepositoryImpl(database: DiaryDatabase) : MeasurementRepository {
    val measurementDao = database.measurementDao

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
        when (id) {
            is MeasurementId.Product -> {
                val measurementId = id.measurementId

                val entity = measurementDao.getProductWeightMeasurementById(
                    id = measurementId,
                    isDeleted = false
                )

                if (entity == null) {
                    Logger.Companion.w(TAG) { "Measurement not found for ID $measurementId." }
                    return
                }

                val quantity = when (weightMeasurement) {
                    is WeightMeasurement.WeightUnit -> weightMeasurement.weight
                    is WeightMeasurement.Package -> weightMeasurement.quantity
                    is WeightMeasurement.Serving -> weightMeasurement.quantity
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
                    Logger.Companion.w(TAG) { "Measurement not found for ID $measurementId." }
                    return
                }

                val quantity = when (weightMeasurement) {
                    is WeightMeasurement.WeightUnit -> weightMeasurement.weight
                    is WeightMeasurement.Package -> weightMeasurement.quantity
                    is WeightMeasurement.Serving -> weightMeasurement.quantity
                }

                val updatedEntity = entity.copy(
                    measurement = weightMeasurement.asEnum(),
                    quantity = quantity
                )

                measurementDao.updateWeightMeasurement(updatedEntity)
            }
        }
    }

    override fun observeMeasurements(mealId: Long?, date: LocalDate): Flow<List<FoodMeasurement>> {
        TODO("Not yet implemented")
    }

    override fun observeMeasurementById(measurementId: MeasurementId): Flow<FoodMeasurement?> {
        TODO("Not yet implemented")
    }

    override fun observeMeasurementSuggestionByFood(foodId: FoodId): Flow<List<WeightMeasurement>> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "MeasurementRepositoryImpl"
    }
}
