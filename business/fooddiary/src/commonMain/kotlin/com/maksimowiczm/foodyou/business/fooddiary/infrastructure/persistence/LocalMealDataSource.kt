package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import kotlinx.coroutines.flow.Flow

internal interface LocalMealDataSource {

    suspend fun observeMealById(mealId: Long): Flow<Meal?>

    suspend fun insert(meal: Meal)

    suspend fun update(meal: Meal)

    suspend fun delete(meal: Meal)
}
