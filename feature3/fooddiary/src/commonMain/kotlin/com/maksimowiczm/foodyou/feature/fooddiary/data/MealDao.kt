package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meal WHERE id = :mealId")
    fun observeMealById(mealId: Long): Flow<Meal?>
}
