package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealsDao {
    @Query(
        """
        SELECT *
        FROM MealEntity
        """
    )
    fun observeMeals(): Flow<List<MealEntity>>
}
