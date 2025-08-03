package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEventDao {

    @Query(
        """
        SELECT * FROM FoodEvent
        WHERE productId = :productId OR recipeId = :recipeId
        ORDER BY epochSeconds ASC
        """
    )
    fun observeEvents(productId: Long?, recipeId: Long?): Flow<List<FoodEvent>>

    fun observeProductEvents(productId: Long): Flow<List<FoodEvent>> =
        observeEvents(productId, null)

    fun observeRecipeEvents(recipeId: Long): Flow<List<FoodEvent>> = observeEvents(null, recipeId)

    @Insert
    suspend fun insert(event: FoodEvent)
}
