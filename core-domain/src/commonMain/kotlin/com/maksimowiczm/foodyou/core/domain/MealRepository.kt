package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.diary.MealEntity
import com.maksimowiczm.foodyou.core.database.diary.MealLocalDataSource
import com.maksimowiczm.foodyou.core.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.LocalTime

interface MealRepository {
    fun observeMeals(): Flow<List<Meal>>

    suspend fun createMeal(name: String, from: LocalTime, to: LocalTime)

    suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime)

    suspend fun deleteMeal(id: Long)

    /**
     * Orders meals by their rank.
     *
     * @param map A map where the key is the meal ID and the value is the new rank.
     */
    suspend fun updateMealsRanks(map: Map<Long, Int>)
}

internal class MealRepositoryImpl(
    private val mealLocalDataSource: MealLocalDataSource,
    private val mealMapper: MealMapper
) : MealRepository {

    override fun observeMeals(): Flow<List<Meal>> =
        mealLocalDataSource.observeMeals().mapValues(mealMapper::toMeal)

    override suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        val entity = MealEntity(
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute,
            rank = 0
        )

        mealLocalDataSource.insertWithLastRank(entity)
    }

    override suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime) {
        val entity = mealLocalDataSource.observeMealById(id).first() ?: return

        val updatedEntity = entity.copy(
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute
        )

        mealLocalDataSource.update(updatedEntity)
    }

    override suspend fun deleteMeal(id: Long) {
        val entity = mealLocalDataSource.observeMealById(id).first() ?: return
        mealLocalDataSource.delete(entity)
    }

    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        mealLocalDataSource.updateMealsRanks(map)
    }
}
