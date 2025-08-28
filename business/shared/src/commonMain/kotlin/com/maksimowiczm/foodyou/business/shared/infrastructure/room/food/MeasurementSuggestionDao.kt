package com.maksimowiczm.foodyou.business.shared.infrastructure.room.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementSuggestionDao {

    @Insert suspend fun insert(measurementSuggestion: MeasurementSuggestionEntity)

    @Query(
        """
        SELECT *
        FROM MeasurementSuggestion
        WHERE
            COALESCE(:productId, -1) = productId
            OR COALESCE(:recipeId, -1) = recipeId
        ORDER BY epochSeconds DESC
        LIMIT :limit
        """
    )
    fun observeByFoodId(
        productId: Long?,
        recipeId: Long?,
        limit: Int,
    ): Flow<List<MeasurementSuggestionEntity>>
}
