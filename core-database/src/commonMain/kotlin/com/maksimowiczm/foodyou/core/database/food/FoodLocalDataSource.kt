package com.maksimowiczm.foodyou.core.database.food

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodLocalDataSource {

    @Query("SELECT * FROM ProductEntity")
    fun observeProducts(): Flow<List<ProductEntity>>
}
