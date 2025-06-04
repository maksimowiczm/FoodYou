package com.maksimowiczm.foodyou.core.database.diary

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLocalDataSource {

    @Query("SELECT * FROM MealEntity")
    fun observeMeals(): Flow<List<MealEntity>>
}
