package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.diary.MealLocalDataSource
import com.maksimowiczm.foodyou.core.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues

interface MealRepository {
    fun observeMeals(): Flow<List<Meal>>
}

internal class MealRepositoryImpl(
    private val mealLocalDataSource: MealLocalDataSource,
    private val mealMapper: MealMapper
) : MealRepository {

    override fun observeMeals(): Flow<List<Meal>> =
        mealLocalDataSource.observeMeals().mapValues(mealMapper::toMeal)
}
