package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import kotlinx.coroutines.flow.Flow

internal interface LocalMealDataSource {

    fun observeAllMeals(): Flow<List<Meal>>

    fun observeMealById(mealId: Long): Flow<Meal?>

    suspend fun insertWithLastRank(meal: Meal)

    suspend fun update(meal: Meal)

    suspend fun delete(meal: Meal)

    suspend fun reorder(order: List<Long>)
}
