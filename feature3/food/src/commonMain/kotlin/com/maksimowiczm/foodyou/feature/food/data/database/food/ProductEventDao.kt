package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductEventDao {

    @Query(
        """
        SELECT * FROM ProductEvent
        WHERE productId = :productId
        ORDER BY epochSeconds ASC
        """
    )
    fun observeEvents(productId: Long): Flow<List<ProductEvent>>

    @Insert
    suspend fun insert(event: ProductEvent)
}
