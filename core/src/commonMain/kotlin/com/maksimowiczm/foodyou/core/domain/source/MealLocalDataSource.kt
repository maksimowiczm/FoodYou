package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.meal.MealEntity
import kotlinx.coroutines.flow.Flow

interface MealLocalDataSource {
    fun observeMeals(): Flow<List<MealEntity>>
    fun observeMeal(id: Long): Flow<MealEntity?>
    suspend fun getMealById(id: Long): MealEntity?
    suspend fun insertWithLastRank(meal: MealEntity)
    suspend fun update(meal: MealEntity)
    suspend fun delete(meal: MealEntity)
    suspend fun updateMealsRanks(map: Map<Long, Int>)
}
