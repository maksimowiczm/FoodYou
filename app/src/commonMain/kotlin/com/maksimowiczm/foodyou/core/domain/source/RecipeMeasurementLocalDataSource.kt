package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeMeasurementEntity
import kotlinx.coroutines.flow.Flow

interface RecipeMeasurementLocalDataSource {
    suspend fun addRecipeMeasurement(entity: RecipeMeasurementEntity)
    suspend fun updateRecipeMeasurement(entity: RecipeMeasurementEntity)
    suspend fun getRecipeMeasurement(id: Long): RecipeMeasurementEntity?
    suspend fun deleteRecipeMeasurement(id: Long)
    fun observeMeasurement(measurementId: Long): Flow<RecipeMeasurementEntity>
    fun observeMeasurements(epochDay: Int, mealId: Long): Flow<List<RecipeMeasurementEntity>>
    fun observeRecipeMeasurementSuggestions(recipeId: Long): Flow<List<MeasurementSuggestion>>
    fun observeLatestRecipeMeasurementSuggestion(recipeId: Long): Flow<MeasurementSuggestion?>

    fun observeMeasurementsByRecipeMealDay(
        recipeId: Long,
        mealId: Long,
        epochDay: Int
    ): Flow<List<RecipeMeasurementEntity>>
}
