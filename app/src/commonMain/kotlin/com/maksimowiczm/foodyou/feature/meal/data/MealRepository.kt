package com.maksimowiczm.foodyou.feature.meal.data

import com.maksimowiczm.foodyou.core.data.model.meal.MealEntity
import com.maksimowiczm.foodyou.core.data.source.MealLocalDataSource
import com.maksimowiczm.foodyou.core.ext.mapValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal interface MealRepository {

    fun observeMeal(id: Long): Flow<Meal?>

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

internal class MealRepositoryImpl(private val mealDao: MealLocalDataSource) : MealRepository {

    override fun observeMeal(id: Long): Flow<Meal?> = mealDao.observeMeal(id).map { entity ->
        entity?.let {
            Meal(
                id = entity.id,
                name = entity.name,
                from = LocalTime(entity.fromHour, entity.fromMinute),
                to = LocalTime(entity.toHour, entity.toMinute),
                rank = entity.rank
            )
        }
    }

    override fun observeMeals(): Flow<List<Meal>> = mealDao.observeMeals().mapValues { entity ->
        Meal(
            id = entity.id,
            name = entity.name,
            from = LocalTime(entity.fromHour, entity.fromMinute),
            to = LocalTime(entity.toHour, entity.toMinute),
            rank = entity.rank
        )
    }

    override suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        val entity = MealEntity(
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute,
            rank = 0
        )

        mealDao.insertWithLastRank(entity)
    }

    override suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime) {
        val entity = mealDao.getMealById(id) ?: return

        val updatedEntity = entity.copy(
            name = name,
            fromHour = from.hour,
            fromMinute = from.minute,
            toHour = to.hour,
            toMinute = to.minute
        )

        mealDao.update(updatedEntity)
    }

    override suspend fun deleteMeal(id: Long) {
        val entity = mealDao.getMealById(id) ?: return
        mealDao.delete(entity)
    }

    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        mealDao.updateMealsRanks(map)
    }
}
