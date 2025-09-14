package com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room

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
    fun observeEvents(productId: Long?, recipeId: Long?): Flow<List<FoodEventEntity>>

    fun observeProductEvents(productId: Long): Flow<List<FoodEventEntity>> =
        observeEvents(productId, null)

    fun observeRecipeEvents(recipeId: Long): Flow<List<FoodEventEntity>> =
        observeEvents(null, recipeId)

    @Insert suspend fun insert(event: FoodEventEntity)
}
