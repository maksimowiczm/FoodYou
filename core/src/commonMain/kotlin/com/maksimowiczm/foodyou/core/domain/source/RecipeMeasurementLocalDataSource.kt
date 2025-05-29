package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.measurement.MeasurementSuggestion
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeWithMeasurement
import kotlinx.coroutines.flow.Flow

interface RecipeMeasurementLocalDataSource {
    suspend fun addRecipeMeasurement(entity: RecipeMeasurementEntity)
    suspend fun updateRecipeMeasurement(entity: RecipeMeasurementEntity)
    suspend fun getRecipeMeasurement(id: Long): RecipeMeasurementEntity?
    suspend fun deleteRecipeMeasurement(id: Long)
    suspend fun restoreRecipeMeasurement(id: Long)
    fun observeRecipeMeasurements(epochDay: Int, mealId: Long): Flow<List<RecipeWithMeasurement>>
    fun observeRecipeMeasurement(measurementId: Long): Flow<RecipeWithMeasurement?>
    fun observeRecipeMeasurementSuggestions(recipeId: Long): Flow<List<MeasurementSuggestion>>
    fun observeLatestRecipeMeasurementSuggestion(recipeId: Long): Flow<MeasurementSuggestion?>
}
